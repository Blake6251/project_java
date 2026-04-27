import { useEffect, useRef } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

const wsBase = import.meta.env.VITE_API_BASE ?? "http://localhost:8080";

/**
 * 관리자 실시간 주문 수신 (/topic/orders)
 * @param {boolean} enabled - true 일 때만 연결
 * @param {(order: object) => void} onOrder - 새 주문 페이로드 콜백
 */
export function useWebSocket(enabled, onOrder) {
  const clientRef = useRef(null);

  useEffect(() => {
    if (!enabled) return undefined;

    const socket = new SockJS(`${wsBase}/ws`);
    const client = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        client.subscribe("/topic/orders", (message) => {
          try {
            const body = JSON.parse(message.body);
            onOrder?.(body);
          } catch {
            /* ignore */
          }
        });
      }
    });
    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
      clientRef.current = null;
    };
  }, [enabled, onOrder]);
}

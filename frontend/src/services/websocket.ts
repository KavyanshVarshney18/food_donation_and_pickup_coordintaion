import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import type { Donation } from "../types";

type EventPayload = { event: string; data: Donation };

export const connectDonationsSocket = (onMessage: (payload: EventPayload) => void) => {
  const client = new Client({
    webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
    reconnectDelay: 5000,
    debug: () => {}
  });

  client.onConnect = () => {
    client.subscribe("/topic/donations", (message) => {
      onMessage(JSON.parse(message.body) as EventPayload);
    });
  };

  client.activate();
  return () => {
    void client.deactivate();
  };
};

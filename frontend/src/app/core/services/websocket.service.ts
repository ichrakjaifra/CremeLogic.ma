import { Injectable, OnDestroy, inject } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { BehaviorSubject, filter, take } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService implements OnDestroy {
  private stompClient: Client | null = null;
  private connectionStatus = new BehaviorSubject<boolean>(false);
  private authService = inject(AuthService);

  constructor() { }

  connect(): void {
    if (this.stompClient && this.stompClient.active) {
      return;
    }

    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(`${environment.apiUrl}/ws-notifications`),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      connectHeaders: {
        Authorization: `Bearer ${this.authService.getToken()}`
      },
      onConnect: (frame) => {
        console.log('Connected to WebSocket');
        this.connectionStatus.next(true);
      },
      onStompError: (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
        this.connectionStatus.next(false);
      },
      onWebSocketClose: () => {
        this.connectionStatus.next(false);
      }
    });

    this.stompClient.activate();
  }

  disconnect(): void {
    if (this.stompClient) {
      this.stompClient.deactivate();
    }
    this.connectionStatus.next(false);
  }

  subscribe(topic: string, callback: (message: any) => void): void {
    this.connectionStatus.pipe(
      filter(status => status),
      take(1)
    ).subscribe(() => {
      if (this.stompClient) {
        this.stompClient.subscribe(topic, (message: IMessage) => {
          callback(JSON.parse(message.body));
        });
      }
    });
  }

  ngOnDestroy(): void {
    this.disconnect();
  }
}

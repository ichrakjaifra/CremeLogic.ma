import { Injectable, OnDestroy } from '@angular/core';
import { Client, IMessage, Stomp } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { BehaviorSubject, Observable, filter, take } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService implements OnDestroy {
  private stompClient: Client | null = null;
  private connectionStatus = new BehaviorSubject<boolean>(false);

  constructor() { }

  connect(): void {
    if (this.stompClient && this.stompClient.connected) {
      return;
    }

    const socket = new (SockJS as any)(`${environment.apiUrl}/ws-notifications`);
    this.stompClient = Stomp.over(socket);

    this.stompClient.onConnect = (frame) => {
      console.log('Connected to WebSocket: ' + frame);
      this.connectionStatus.next(true);
    };

    this.stompClient.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      console.error('Additional details: ' + frame.body);
      this.connectionStatus.next(false);
    };

    this.stompClient.activate();
  }

  disconnect(): void {
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.connectionStatus.next(false);
    }
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

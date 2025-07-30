import { Component, OnInit } from '@angular/core';
import {
  MessageResponse,
  MessageService,
} from 'src/app/services/message.service';
import { SessionService } from 'src/app/services/session.service';

export interface Message {
  id: number;
  rental_id: number;
  user_id: number;
  message: string;
  created_at: string;
  updated_at: string;
}

@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.scss'],
})
export class MessagesComponent implements OnInit {
  public messages: MessageResponse[] = [];
  public loading = true;
  public error: string | null = null;
  public unreadCount = 0;

  constructor(
    private messageService: MessageService,
    private sessionService: SessionService
  ) {}

  ngOnInit(): void {
    this.loadMessages();
  }

  /**
   * Load all messages for the current user
   */
  private loadMessages(): void {
    this.loading = true;
    this.error = null;

    this.messageService.getMyMessages().subscribe({
      next: (messages: MessageResponse[]) => {
        this.messages = messages;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading messages:', error);
        this.error = 'Failed to load messages';
        this.loading = false;
      },
    });
  }


  /**
   * Check if current user is the sender
   */
  public isSender(message: MessageResponse): boolean {
    return this.sessionService.user?.id === message.user_id;
  }

  /**
   * Get message type for styling
   */
  public getMessageType(message: MessageResponse): 'sent' | 'received' {
    return this.isSender(message) ? 'sent' : 'received';
  }

  /**
   * Refresh messages
   */
  public refreshMessages(): void {
    this.loadMessages();
  }

  /**
   * Format date for display
   */
  public formatDate(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffTime = Math.abs(now.getTime() - date.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays === 1) {
      return (
        'Today ' +
        date.toLocaleTimeString('en-US', {
          hour: '2-digit',
          minute: '2-digit',
        })
      );
    } else if (diffDays === 2) {
      return (
        'Yesterday ' +
        date.toLocaleTimeString('en-US', {
          hour: '2-digit',
          minute: '2-digit',
        })
      );
    } else {
      return date.toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      });
    }
  }

  /**
   * TrackBy function for ngFor performance optimization
   */
  public trackByMessageId(index: number, message: MessageResponse): number {
    return message.id;
  }
}

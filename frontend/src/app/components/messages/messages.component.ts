import { Component, OnInit } from '@angular/core';
import { MessageService } from 'src/app/services/message.service';
import { SessionService } from 'src/app/services/session.service';

export interface Message {
  id: number;
  message: string;
  rental_id: number;
  rental_name: string;
  sender_id: number;
  sender_name: string;
  sender_email: string;
  recipient_id: number;
  recipient_name: string;
  recipient_email: string;
  is_read: boolean;
  created_at: string;
  updated_at: string;
}

@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.scss']
})
export class MessagesComponent implements OnInit {

  public messages: Message[] = [];
  public loading = true;
  public error: string | null = null;
  public unreadCount = 0;

  constructor(
    private messageService: MessageService,
    private sessionService: SessionService
  ) {}

  ngOnInit(): void {
    this.loadMessages();
    this.loadUnreadCount();
  }

  /**
   * Load all messages for the current user
   */
  private loadMessages(): void {
    this.loading = true;
    this.error = null;

    this.messageService.getMyMessages().subscribe({
      next: (messages: Message[]) => {
        this.messages = messages;
        this.loading = false;
        console.log('📨 Messages loaded:', messages);
      },
      error: (error) => {
        console.error('❌ Error loading messages:', error);
        this.error = 'Failed to load messages';
        this.loading = false;
      }
    });
  }

  /**
   * Load unread message count
   */
  private loadUnreadCount(): void {
    this.messageService.getUnreadCount().subscribe({
      next: (response: { count: number }) => {
        this.unreadCount = response.count;
        console.log('🔔 Unread count:', this.unreadCount);
      },
      error: (error) => {
        console.error('❌ Error loading unread count:', error);
      }
    });
  }

  /**
   * Mark message as read
   */
  public markAsRead(message: Message): void {
    if (message.is_read) {
      return; // Already read
    }

    // Only recipients can mark as read
    const currentUserId = this.sessionService.user?.id;
    if (currentUserId !== message.recipient_id) {
      return;
    }

    this.messageService.markAsRead(message.id).subscribe({
      next: () => {
        message.is_read = true;
        this.unreadCount = Math.max(0, this.unreadCount - 1);
        console.log('✅ Message marked as read:', message.id);
      },
      error: (error) => {
        console.error('❌ Error marking message as read:', error);
      }
    });
  }

  /**
   * Check if current user is the sender
   */
  public isSender(message: Message): boolean {
    return this.sessionService.user?.id === message.sender_id;
  }

  /**
   * Check if current user is the recipient
   */
  public isRecipient(message: Message): boolean {
    return this.sessionService.user?.id === message.recipient_id;
  }

  /**
   * Get message type for styling
   */
  public getMessageType(message: Message): 'sent' | 'received' {
    return this.isSender(message) ? 'sent' : 'received';
  }

  /**
   * Refresh messages
   */
  public refreshMessages(): void {
    this.loadMessages();
    this.loadUnreadCount();
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
      return 'Today ' + date.toLocaleTimeString('en-US', { 
        hour: '2-digit', 
        minute: '2-digit' 
      });
    } else if (diffDays === 2) {
      return 'Yesterday ' + date.toLocaleTimeString('en-US', { 
        hour: '2-digit', 
        minute: '2-digit' 
      });
    } else {
      return date.toLocaleDateString('en-US', { 
        month: 'short', 
        day: 'numeric',
        hour: '2-digit', 
        minute: '2-digit'
      });
    }
  }

  /**
   * Get contact name (sender or recipient depending on perspective)
   */
  public getContactName(message: Message): string {
    return this.isSender(message) ? message.recipient_name : message.sender_name;
  }

  /**
   * TrackBy function for ngFor performance optimization
   */
  public trackByMessageId(index: number, message: Message): number {
    return message.id;
  }
}

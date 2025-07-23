// ========================================
// message.service.ts
// ========================================
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface MessageRequest {
  message: string;
  user_id: number;
  rental_id: number;
}

export interface MessageResponse {
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

export interface SendMessageResponse {
  message: string;
}

export interface UnreadCountResponse {
  count: number;
}

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  private pathService = 'http://localhost:3001/api/messages';

  constructor(private httpClient: HttpClient) {}

  /**
   * Send a new message
   */
  public sendMessage(messageData: MessageRequest): Observable<SendMessageResponse> {
    console.log('📤 Sending message:', messageData);
    
    return this.httpClient.post<SendMessageResponse>(this.pathService, messageData);
  }

  /**
   * Get all messages for the current user
   */
  public getMyMessages(): Observable<MessageResponse[]> {
    console.log('📥 Fetching my messages...');
    
    return this.httpClient.get<MessageResponse[]>(this.pathService);
  }

  /**
   * Get messages for a specific rental
   */
  public getMessagesByRental(rentalId: number): Observable<MessageResponse[]> {
    console.log('📥 Fetching messages for rental:', rentalId);
    
    return this.httpClient.get<MessageResponse[]>(`${this.pathService}/rental/${rentalId}`);
  }

  /**
   * Get a specific message by ID
   */
  public getMessageById(messageId: number): Observable<MessageResponse> {
    console.log('📥 Fetching message:', messageId);
    
    return this.httpClient.get<MessageResponse>(`${this.pathService}/${messageId}`);
  }

  /**
   * Mark a message as read
   */
  public markAsRead(messageId: number): Observable<{message: string}> {
    console.log('✅ Marking message as read:', messageId);
    
    return this.httpClient.put<{message: string}>(`${this.pathService}/${messageId}/read`, {});
  }

  /**
   * Get unread message count
   */
  public getUnreadCount(): Observable<UnreadCountResponse> {
    console.log('🔔 Fetching unread count...');
    
    return this.httpClient.get<UnreadCountResponse>(`${this.pathService}/unread/count`);
  }
}
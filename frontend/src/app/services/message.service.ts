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
  user_id: number;
  rental_id: number;
  message: string;
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
    return this.httpClient.post<SendMessageResponse>(this.pathService, messageData);
  }

  /**
   * Get all messages for the current user
   */
  public getMyMessages(): Observable<MessageResponse[]> {
    return this.httpClient.get<MessageResponse[]>(this.pathService);
  }

  /**
   * Get messages for a specific rental
   */
  public getMessagesByRental(rentalId: number): Observable<MessageResponse[]> {
    return this.httpClient.get<MessageResponse[]>(`${this.pathService}/rental/${rentalId}`);
  }

  /**
   * Get a specific message by ID
   */
  public getMessageById(messageId: number): Observable<MessageResponse> {
    return this.httpClient.get<MessageResponse>(`${this.pathService}/${messageId}`);
  }

}
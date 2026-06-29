import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ConsultaSeguimientoResponse, ConfirmarEmailRequest } from '../models/bolsin.model';

@Injectable({
  providedIn: 'root'
})
export class SeguimientoService {

  private readonly API_URL = '/api/seguimiento';

  constructor(private http: HttpClient) {}

  iniciarConsulta(): Observable<ConsultaSeguimientoResponse> {
    return this.http.get<ConsultaSeguimientoResponse>(`${this.API_URL}/iniciar`);
  }

  seleccionarBolsin(numeroBolsin: number): Observable<ConsultaSeguimientoResponse> {
    return this.http.post<ConsultaSeguimientoResponse>(
      `${this.API_URL}/seleccionar/${numeroBolsin}`, {}
    );
  }

  confirmarEmail(numeroBolsin: number, confirma: boolean): Observable<ConsultaSeguimientoResponse> {
    const body: ConfirmarEmailRequest = { confirma };
    return this.http.post<ConsultaSeguimientoResponse>(
      `${this.API_URL}/confirmar-email/${numeroBolsin}`, body
    );
  }
}

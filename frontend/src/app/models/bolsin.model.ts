export interface BolsinDTO {
  numeroBolsin: number;
  numeroPrecinto: number;
  cmDestinoNombre: string;
  cmOrigenNombre: string;
  latitud: number | null;
  longitud: number | null;
  fechaHoraActualizacion: string | null;
}

export interface ConsultaSeguimientoResponse {
  nombreCMUsuarioLogueado: string | null;
  nombreUsuarioLogueado: string | null;
  bolsines: BolsinDTO[] | null;
  mensaje: string | null;
}

export interface ConfirmarEmailRequest {
  confirma: boolean;
}

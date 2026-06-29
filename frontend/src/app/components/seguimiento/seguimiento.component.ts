import { Component, OnInit, AfterViewInit, OnDestroy, NgZone, ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SeguimientoService } from '../../services/seguimiento.service';
import { BolsinDTO, ConsultaSeguimientoResponse } from '../../models/bolsin.model';
import * as L from 'leaflet';

delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',
});

@Component({
  selector: 'app-seguimiento',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './seguimiento.component.html',
  styleUrl: './seguimiento.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SeguimientoComponent implements OnInit, AfterViewInit, OnDestroy {

  nombreCMUsuarioLogueado: string = '';
  nombreUsuarioLogueado: string = '';
  posicionBolsin: BolsinDTO[] = [];
  bolsinSeleccionado: BolsinDTO | null = null;
  realizacionExitosaDelCU: string = '';

  cargando: boolean = false;
  error: string = '';
  confirmacionEmail: boolean = false;
  filtroPrecinto: string = '';
  filtroCMDestino: string = '';

  private mapa: L.Map | null = null;
  private markers: L.Marker[] = [];
  private resizeObserver: ResizeObserver | null = null;

  constructor(
    private seguimientoService: SeguimientoService,
    private zone: NgZone,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.opcionConsultarSeguimiento();
  }

  ngAfterViewInit(): void {
  }

  ngOnDestroy(): void {
    this.resizeObserver?.disconnect();
    if (this.mapa) {
      this.mapa.remove();
    }
  }

  opcionConsultarSeguimiento(): void {
    this.habilitarVentana();

    this.seguimientoService.iniciarConsulta().subscribe({
      next: (response: ConsultaSeguimientoResponse) => {
        this.cargando = false;
        this.mostrarCMUsuarioLogueado(response.nombreCMUsuarioLogueado ?? '');
        this.mostrarNombreUsuarioLogueado(response.nombreUsuarioLogueado ?? '');
        if (response.mensaje && (!response.bolsines || response.bolsines.length === 0)) {
          this.error = response.mensaje;
          this.cdr.markForCheck();
          return;
        }
        this.mostrarPosicionBolsin(response.bolsines ?? []);
        this.solicitarSeleccionBolsin();
      },
      error: () => {
        this.cargando = false;
        this.error = 'Error al conectar con el servidor. Verifique que el backend esté en ejecución.';
        this.cdr.markForCheck();
      }
    });
  }

  habilitarVentana(): void {
    this.cargando = true;
    this.error = '';
    this.realizacionExitosaDelCU = '';
    this.posicionBolsin = [];
  }

  mostrarCMUsuarioLogueado(nombre: string): void {
    this.nombreCMUsuarioLogueado = nombre;
    this.cdr.markForCheck();
  }

  mostrarNombreUsuarioLogueado(nombre: string): void {
    this.nombreUsuarioLogueado = nombre;
    this.cdr.markForCheck();
  }

  mostrarPosicionBolsin(bolsines: BolsinDTO[]): void {
    this.posicionBolsin = bolsines;
    this.cdr.markForCheck();
    setTimeout(() => {
      this.inicializarMapa();
      this.mapa?.invalidateSize();
      this.actualizarMarcadores();
    }, 150);
  }

  solicitarSeleccionBolsin(): void {
  }

  get bolsinesFiltrados(): BolsinDTO[] {
    return this.posicionBolsin.filter(b => {
      const filtroPrecinto = this.filtroPrecinto.trim();
      const filtroCM = this.filtroCMDestino.trim().toLowerCase();
      const coincidePrecinto = filtroPrecinto === '' || b.numeroPrecinto.toString().includes(filtroPrecinto);
      const coincideCM = filtroCM === '' || b.cmDestinoNombre.toLowerCase().includes(filtroCM);
      return coincidePrecinto && coincideCM;
    });
  }

  tomarSeleccionBolsin(bolsin: BolsinDTO): void {
    this.bolsinSeleccionado = bolsin;
    this.confirmacionEmail = false;
    this.realizacionExitosaDelCU = '';

    this.seguimientoService.seleccionarBolsin(bolsin.numeroBolsin).subscribe({
      next: () => {
        this.consultarEnvioEmail();
        if (bolsin.latitud && bolsin.longitud) {
          this.mapa?.setView([bolsin.latitud, bolsin.longitud], 10);
        }
        this.cdr.markForCheck();
      },
      error: () => {
        this.error = 'Error al seleccionar el bolsín.';
        this.cdr.markForCheck();
      }
    });
  }

  consultarEnvioEmail(): void {
    this.confirmacionEmail = true;
    this.cdr.markForCheck();
  }

  tomarConfirmacionEmail(confirma: boolean): void {
    if (!this.bolsinSeleccionado) return;
    this.confirmacionEmail = false;

    this.seguimientoService.confirmarEmail(this.bolsinSeleccionado.numeroBolsin, confirma).subscribe({
      next: (response: ConsultaSeguimientoResponse) => {
        this.mostrarRealizacionExitosaDelCU(response.mensaje ?? (confirma ? 'Operación completada.' : 'Consulta finalizada.'));
      },
      error: () => {
        this.error = 'Error al confirmar el email.';
        this.cdr.markForCheck();
      }
    });
  }

  mostrarRealizacionExitosaDelCU(mensaje: string): void {
    this.realizacionExitosaDelCU = mensaje;
    this.cdr.markForCheck();
  }

  actualizarFiltros(tipo: string, valor: string): void {
    if (tipo === 'precinto') this.filtroPrecinto = valor;
    if (tipo === 'cm') this.filtroCMDestino = valor;
  }

  private inicializarMapa(): void {
    if (this.mapa) return;
    const contenedor = document.getElementById('mapa');
    if (!contenedor) return;

    this.mapa = L.map('mapa').setView([-34.6037, -58.3816], 5);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.mapa);

    this.resizeObserver = new ResizeObserver(() => {
      this.zone.runOutsideAngular(() => this.mapa?.invalidateSize());
    });
    this.resizeObserver.observe(contenedor);
  }

  private actualizarMarcadores(): void {
    if (!this.mapa) return;

    this.markers.forEach(m => m.remove());
    this.markers = [];

    const bounds: [number, number][] = [];

    this.posicionBolsin.forEach(bolsin => {
      if (bolsin.latitud && bolsin.longitud) {

        const iconoBolsin = L.divIcon({
          className: 'marker-bolsin-wrapper',
          html: `
            <div class="marker-label">#${bolsin.numeroBolsin}</div>
            <img
              class="marker-pin"
              src="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon.png"
            >
          `,
          iconSize: [70, 72],
          iconAnchor: [35, 72],
          popupAnchor: [0, -72]
        });

        const marker = L.marker([bolsin.latitud, bolsin.longitud], { icon: iconoBolsin })
          .addTo(this.mapa!)
          .bindPopup(this.armarPopupBolsin(bolsin));

        marker.on('click', () => this.zone.run(() => this.tomarSeleccionBolsin(bolsin)));

        this.markers.push(marker);
        bounds.push([bolsin.latitud, bolsin.longitud]);
      }
    });

    if (bounds.length > 0) {
      this.mapa.fitBounds(bounds, { padding: [50, 50] });
    }
  }

  private armarPopupBolsin(bolsin: BolsinDTO): string {
    return `
      <div class="popup-bolsin">
        <strong>Bolsín #${bolsin.numeroBolsin}</strong><br>
        Precinto: ${bolsin.numeroPrecinto}<br>
        Origen: ${bolsin.cmOrigenNombre ?? 'N/D'}<br>
        Destino: ${bolsin.cmDestinoNombre}<br>
        Actualizado: ${bolsin.fechaHoraActualizacion ?? 'N/D'}<br>
        <small>Lat: ${bolsin.latitud?.toFixed(4)}, Lng: ${bolsin.longitud?.toFixed(4)}</small>
      </div>
    `;
  }
}

// src/app/models/noticia.model.ts
export interface Noticia {
  id?: number;
  titulo: string;
  contenido: string;
  palabras?: number;
  corresponsalId?: number;
  corresponsal?: {
    id?: number;
    fullName?: string;
    numeroDocumento?: number;
    ubigeo?: string;
    centroPoblado?: string;
    isActive?: boolean;
  };
  fotos?: string[]; // Rutas de archivos
  videoPath?: string;
  active?: boolean;
  publishDate?: Date;
  createdAt?: Date;
  updatedAt?: Date;
  deletedAt?: Date;
}
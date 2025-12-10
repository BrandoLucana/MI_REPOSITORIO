// src/app/models/corresponsal.model.ts
export interface Corresponsal {
  id?: number;
  fullName: string;
  numeroDocumento: number;
  ubigeo: string;
  centroPoblado: string;
  isActive?: boolean;
  fechaRegistro?: Date;
  createdAt?: Date;
  updatedAt?: Date;
  deletedAt?: Date;
}
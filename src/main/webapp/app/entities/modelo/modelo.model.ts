import { IMarca } from 'app/entities/marca/marca.model';

export interface IModelo {
  id?: number;
  modelo?: string;
  color?: string | null;
  potencia?: number | null;
  plazas?: number | null;
  precio?: number | null;
  marca?: IMarca | null;
  numero_serie?: string;
}

export class Modelo implements IModelo {
  constructor(
    public id?: number,
    public modelo?: string,
    public color?: string | null,
    public potencia?: number | null,
    public plazas?: number | null,
    public precio?: number | null,
    public marca?: IMarca | null,
    public numero_serie?: string
  ) {}
}

export function getModeloIdentifier(modelo: IModelo): number | undefined {
  return modelo.id;
}

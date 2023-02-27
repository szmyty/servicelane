import { IUser } from 'app/shared/model/user.model';

export interface IVehicle {
  id?: number;
  make?: string | null;
  model?: string | null;
  color?: string | null;
  vin?: string | null;
  owner?: IUser | null;
}

export const defaultValue: Readonly<IVehicle> = {};

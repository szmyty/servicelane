import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';

export interface IAppointment {
  id?: number;
  created?: string;
  startTime?: string;
  endTime?: string;
  services?: string;
  client?: IUser | null;
}

export const defaultValue: Readonly<IAppointment> = {};

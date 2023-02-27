
export type Appointment = {
  uuid: string;
  created: string;
  services: Array<string>;
  user: string;
}

export type CreateAppointment = Pick<Appointment, "user">

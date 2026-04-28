import apiClient from './client'

export interface ReservationSeat {
  seatId: number
  sectionName: string
  row: string
  number: number
}

export interface Reservation {
  id: number
  gameId: number
  gameTitle: string
  gameDateTime: string
  stadium: string
  totalPrice: number
  status: string
  qrCode: string
  seats: ReservationSeat[]
  createdAt: string
}

export interface PaymentResponse {
  id: number
  reservationId: number
  amount: number
  status: string
  method: string
  pgTransactionId: string
  paidAt: string
}

export const reservationsApi = {
  holdSeats: (seatIds: number[]) =>
    apiClient.post('/seats/hold', { seatIds }),

  releaseSeats: (seatIds: number[]) =>
    apiClient.delete('/seats/hold', { data: seatIds }),

  create: (data: { gameId: number; seatIds: number[] }) =>
    apiClient.post<Reservation>('/reservations', data).then((r) => r.data),

  list: () =>
    apiClient.get<Reservation[]>('/reservations').then((r) => r.data),

  get: (reservationId: number) =>
    apiClient.get<Reservation>(`/reservations/${reservationId}`).then((r) => r.data),

  cancel: (reservationId: number) =>
    apiClient.delete(`/reservations/${reservationId}`),

  pay: (data: { reservationId: number; method: string }) =>
    apiClient.post<PaymentResponse>('/payments', data).then((r) => r.data),
}

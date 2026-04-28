import apiClient from './client'

export interface Team {
  id: number
  name: string
  shortName: string
  logoUrl: string
}

export interface Game {
  id: number
  homeTeam: Team
  awayTeam: Team
  gameDateTime: string
  stadium: string
  saleStartAt: string
  status: string
  totalAvailableSeats: number
}

export interface Section {
  id: number
  name: string
  type: string
  price: number
  totalSeats: number
  availableSeats: number
}

export interface Seat {
  id: number
  row: string
  number: number
  status: 'AVAILABLE' | 'HELD' | 'BOOKED'
}

export const gamesApi = {
  getGames: (params?: { date?: string; teamId?: number }) =>
    apiClient.get<Game[]>('/games', { params }).then((r) => r.data),

  getGame: (gameId: number) =>
    apiClient.get<Game>(`/games/${gameId}`).then((r) => r.data),

  getSections: (gameId: number) =>
    apiClient.get<Section[]>(`/games/${gameId}/sections`).then((r) => r.data),

  getSeats: (sectionId: number) =>
    apiClient.get<Seat[]>(`/games/sections/${sectionId}/seats`).then((r) => r.data),
}

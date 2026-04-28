import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { gamesApi, Game } from '../api/games'
import { format } from 'date-fns'
import { ko } from 'date-fns/locale'

export default function HomePage() {
  const { data: games, isLoading } = useQuery({
    queryKey: ['games'],
    queryFn: () => gamesApi.getGames(),
  })

  const statusLabel = (status: string) => {
    switch (status) {
      case 'ON_SALE': return { text: '예매중', cls: 'bg-green-100 text-green-700' }
      case 'SCHEDULED': return { text: '예매예정', cls: 'bg-yellow-100 text-yellow-700' }
      case 'SOLD_OUT': return { text: '매진', cls: 'bg-red-100 text-red-700' }
      default: return { text: status, cls: 'bg-gray-100 text-gray-600' }
    }
  }

  if (isLoading) return <div className="text-center py-20 text-gray-500">경기 불러오는 중...</div>

  return (
    <div>
      <h1 className="text-2xl font-bold mb-6">이번 달 경기 일정</h1>
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {games?.map((game: Game) => {
          const { text, cls } = statusLabel(game.status)
          const canBook = game.status === 'ON_SALE' && game.totalAvailableSeats > 0
          return (
            <div key={game.id} className="bg-white rounded-xl shadow-sm border p-5 hover:shadow-md transition">
              <div className="flex justify-between items-start mb-3">
                <span className={`text-xs font-semibold px-2 py-1 rounded-full ${cls}`}>{text}</span>
                <span className="text-xs text-gray-400">{game.totalAvailableSeats}석 남음</span>
              </div>
              <div className="flex items-center justify-center gap-4 my-4">
                <div className="text-center">
                  <div className="text-2xl font-bold text-blue-700">{game.homeTeam.shortName}</div>
                  <div className="text-xs text-gray-500">{game.homeTeam.name}</div>
                </div>
                <div className="text-gray-400 font-bold">VS</div>
                <div className="text-center">
                  <div className="text-2xl font-bold text-red-600">{game.awayTeam.shortName}</div>
                  <div className="text-xs text-gray-500">{game.awayTeam.name}</div>
                </div>
              </div>
              <div className="text-sm text-gray-600 space-y-1">
                <div>📅 {format(new Date(game.gameDateTime), 'M월 d일 (E) HH:mm', { locale: ko })}</div>
                <div>🏟️ {game.stadium}</div>
              </div>
              <Link
                to={canBook ? `/games/${game.id}` : '#'}
                className={`mt-4 block text-center py-2 rounded-lg text-sm font-medium transition ${
                  canBook
                    ? 'bg-blue-600 text-white hover:bg-blue-700'
                    : 'bg-gray-100 text-gray-400 cursor-not-allowed'
                }`}
              >
                {canBook ? '예매하기' : text}
              </Link>
            </div>
          )
        })}
      </div>
      {games?.length === 0 && (
        <div className="text-center py-20 text-gray-400">이번 달 예정된 경기가 없습니다.</div>
      )}
    </div>
  )
}

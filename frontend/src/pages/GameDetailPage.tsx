import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useQuery, useMutation } from '@tanstack/react-query'
import { gamesApi, Section, Seat } from '../api/games'
import { reservationsApi } from '../api/reservations'
import { useAuthStore } from '../store/authStore'
import { format } from 'date-fns'
import { ko } from 'date-fns/locale'

export default function GameDetailPage() {
  const { gameId } = useParams<{ gameId: string }>()
  const navigate = useNavigate()
  const { isAuthenticated } = useAuthStore()

  const [selectedSection, setSelectedSection] = useState<Section | null>(null)
  const [selectedSeats, setSelectedSeats] = useState<number[]>([])
  const [holdError, setHoldError] = useState('')

  const { data: game } = useQuery({
    queryKey: ['game', gameId],
    queryFn: () => gamesApi.getGame(Number(gameId)),
  })

  const { data: sections } = useQuery({
    queryKey: ['sections', gameId],
    queryFn: () => gamesApi.getSections(Number(gameId)),
    enabled: !!gameId,
  })

  const { data: seats, isLoading: seatsLoading } = useQuery({
    queryKey: ['seats', selectedSection?.id],
    queryFn: () => gamesApi.getSeats(selectedSection!.id),
    enabled: !!selectedSection,
  })

  const holdMutation = useMutation({
    mutationFn: (seatIds: number[]) => reservationsApi.holdSeats(seatIds),
    onSuccess: () => {
      navigate('/checkout', {
        state: {
          gameId: Number(gameId),
          seatIds: selectedSeats,
          sectionName: selectedSection?.name,
          price: selectedSection!.price * selectedSeats.length,
        },
      })
    },
    onError: (err: any) => {
      setHoldError(err.response?.data?.message || '좌석 선택에 실패했습니다.')
    },
  })

  const toggleSeat = (seat: Seat) => {
    if (seat.status !== 'AVAILABLE') return
    setSelectedSeats((prev) =>
      prev.includes(seat.id)
        ? prev.filter((id) => id !== seat.id)
        : prev.length < 4
        ? [...prev, seat.id]
        : prev
    )
  }

  const handleBook = () => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }
    if (selectedSeats.length === 0) return
    holdMutation.mutate(selectedSeats)
  }

  if (!game) return <div className="text-center py-20">로딩 중...</div>

  return (
    <div className="max-w-3xl mx-auto">
      {/* 경기 정보 헤더 */}
      <div className="bg-white rounded-xl shadow-sm border p-6 mb-6">
        <div className="flex items-center justify-center gap-8 mb-4">
          <div className="text-center">
            <div className="text-3xl font-bold text-blue-700">{game.homeTeam.shortName}</div>
            <div className="text-sm text-gray-500">{game.homeTeam.name}</div>
          </div>
          <div className="text-xl text-gray-400 font-bold">VS</div>
          <div className="text-center">
            <div className="text-3xl font-bold text-red-600">{game.awayTeam.shortName}</div>
            <div className="text-sm text-gray-500">{game.awayTeam.name}</div>
          </div>
        </div>
        <div className="text-center text-gray-600 space-y-1 text-sm">
          <div>📅 {format(new Date(game.gameDateTime), 'yyyy년 M월 d일 (E) HH:mm', { locale: ko })}</div>
          <div>🏟️ {game.stadium}</div>
        </div>
      </div>

      {/* 구역 선택 */}
      <div className="mb-6">
        <h2 className="text-lg font-semibold mb-3">구역 선택</h2>
        <div className="grid grid-cols-2 gap-3 md:grid-cols-3">
          {sections?.map((section) => (
            <button
              key={section.id}
              onClick={() => { setSelectedSection(section); setSelectedSeats([]) }}
              className={`p-4 rounded-lg border text-left transition ${
                selectedSection?.id === section.id
                  ? 'border-blue-500 bg-blue-50'
                  : 'border-gray-200 hover:border-blue-300 bg-white'
              }`}
            >
              <div className="font-medium text-sm">{section.name}</div>
              <div className="text-blue-600 font-bold">{section.price.toLocaleString()}원</div>
              <div className="text-xs text-gray-400">{section.availableSeats}석 가능</div>
            </button>
          ))}
        </div>
      </div>

      {/* 좌석 배치도 */}
      {selectedSection && (
        <div className="mb-6">
          <h2 className="text-lg font-semibold mb-3">좌석 선택 (최대 4석)</h2>
          <div className="bg-gray-100 rounded p-2 mb-2 text-xs text-center text-gray-500">
            ← 홈 플레이트 방향 →
          </div>
          {seatsLoading ? (
            <div className="text-center py-10 text-gray-400">좌석 불러오는 중...</div>
          ) : (
            <div className="overflow-x-auto">
              {Array.from(new Set(seats?.map((s) => s.row))).map((row) => (
                <div key={row} className="flex items-center gap-1 mb-1">
                  <span className="w-5 text-xs text-gray-400 text-right">{row}</span>
                  <div className="flex gap-1 flex-wrap">
                    {seats?.filter((s) => s.row === row).map((seat) => (
                      <button
                        key={seat.id}
                        onClick={() => toggleSeat(seat)}
                        disabled={seat.status !== 'AVAILABLE'}
                        className={`w-7 h-7 rounded text-xs font-medium transition ${
                          seat.status === 'BOOKED'
                            ? 'bg-gray-300 text-gray-400 cursor-not-allowed'
                            : seat.status === 'HELD'
                            ? 'bg-yellow-200 text-yellow-700 cursor-not-allowed'
                            : selectedSeats.includes(seat.id)
                            ? 'bg-blue-600 text-white'
                            : 'bg-green-100 text-green-700 hover:bg-green-200'
                        }`}
                        title={`${row}열 ${seat.number}번`}
                      >
                        {seat.number}
                      </button>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          )}
          <div className="flex gap-4 text-xs mt-3">
            <span className="flex items-center gap-1"><span className="w-4 h-4 bg-green-100 rounded inline-block" /> 선택가능</span>
            <span className="flex items-center gap-1"><span className="w-4 h-4 bg-blue-600 rounded inline-block" /> 선택됨</span>
            <span className="flex items-center gap-1"><span className="w-4 h-4 bg-yellow-200 rounded inline-block" /> 임시점유</span>
            <span className="flex items-center gap-1"><span className="w-4 h-4 bg-gray-300 rounded inline-block" /> 예매완료</span>
          </div>
        </div>
      )}

      {/* 예매 버튼 */}
      {selectedSeats.length > 0 && (
        <div className="sticky bottom-4 bg-white rounded-xl shadow-lg border p-4 flex items-center justify-between">
          <div>
            <div className="text-sm text-gray-600">{selectedSeats.length}석 선택</div>
            <div className="font-bold text-lg">
              {(selectedSection!.price * selectedSeats.length).toLocaleString()}원
            </div>
          </div>
          {holdError && <div className="text-red-500 text-sm">{holdError}</div>}
          <button
            onClick={handleBook}
            disabled={holdMutation.isPending}
            className="bg-blue-600 text-white px-6 py-3 rounded-lg font-medium hover:bg-blue-700 disabled:opacity-50"
          >
            {holdMutation.isPending ? '처리 중...' : '예매하기'}
          </button>
        </div>
      )}
    </div>
  )
}

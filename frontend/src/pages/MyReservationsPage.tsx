import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { reservationsApi } from '../api/reservations'
import { format } from 'date-fns'
import { ko } from 'date-fns/locale'

export default function MyReservationsPage() {
  const queryClient = useQueryClient()
  const { data: reservations, isLoading } = useQuery({
    queryKey: ['myReservations'],
    queryFn: () => reservationsApi.list(),
  })

  const cancelMutation = useMutation({
    mutationFn: (id: number) => reservationsApi.cancel(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['myReservations'] }),
  })

  const statusBadge = (status: string) => {
    switch (status) {
      case 'CONFIRMED': return 'bg-green-100 text-green-700'
      case 'PENDING': return 'bg-yellow-100 text-yellow-700'
      default: return 'bg-gray-100 text-gray-500'
    }
  }

  if (isLoading) return <div className="text-center py-20">로딩 중...</div>

  return (
    <div>
      <h1 className="text-2xl font-bold mb-6">내 예매 내역</h1>
      {reservations?.length === 0 ? (
        <div className="text-center py-20 text-gray-400">예매 내역이 없습니다.</div>
      ) : (
        <div className="space-y-4">
          {reservations?.map((r) => (
            <div key={r.id} className="bg-white rounded-xl shadow-sm border p-5">
              <div className="flex justify-between items-start mb-3">
                <div>
                  <span className="font-bold text-lg">{r.gameTitle}</span>
                  <span className={`ml-2 text-xs px-2 py-1 rounded-full ${statusBadge(r.status)}`}>
                    {r.status === 'CONFIRMED' ? '예매완료' : '결제대기'}
                  </span>
                </div>
                <span className="text-blue-600 font-bold">{r.totalPrice.toLocaleString()}원</span>
              </div>
              <div className="text-sm text-gray-600 space-y-1">
                <div>📅 {format(new Date(r.gameDateTime), 'M월 d일 (E) HH:mm', { locale: ko })}</div>
                <div>🏟️ {r.stadium}</div>
                <div>🪑 {r.seats.map((s) => `${s.sectionName} ${s.row}열 ${s.number}번`).join(', ')}</div>
              </div>
              <div className="flex gap-2 mt-4">
                <Link
                  to={`/reservations/${r.id}`}
                  className="flex-1 text-center py-2 border border-blue-500 text-blue-600 rounded-lg text-sm hover:bg-blue-50"
                >
                  티켓 보기
                </Link>
                {r.status !== 'CANCELLED' && (
                  <button
                    onClick={() => {
                      if (confirm('예매를 취소하시겠습니까?')) cancelMutation.mutate(r.id)
                    }}
                    className="flex-1 py-2 border border-red-300 text-red-500 rounded-lg text-sm hover:bg-red-50"
                  >
                    취소
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

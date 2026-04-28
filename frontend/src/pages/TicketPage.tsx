import { useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { reservationsApi } from '../api/reservations'
import { QRCodeSVG } from 'qrcode.react'
import { format } from 'date-fns'
import { ko } from 'date-fns/locale'

export default function TicketPage() {
  const { reservationId } = useParams<{ reservationId: string }>()

  const { data: reservation, isLoading } = useQuery({
    queryKey: ['reservation', reservationId],
    queryFn: () => reservationsApi.get(Number(reservationId)),
  })

  if (isLoading) return <div className="text-center py-20">로딩 중...</div>
  if (!reservation) return <div className="text-center py-20 text-red-500">예매를 찾을 수 없습니다.</div>

  const statusLabel = reservation.status === 'CONFIRMED' ? '✅ 예매 완료' : '⏳ 결제 대기'

  return (
    <div className="max-w-sm mx-auto">
      <div className="bg-white rounded-2xl shadow-lg border overflow-hidden">
        {/* 헤더 */}
        <div className="bg-blue-700 text-white p-5 text-center">
          <div className="text-sm mb-1">{statusLabel}</div>
          <div className="text-2xl font-bold">{reservation.gameTitle}</div>
          <div className="text-sm text-blue-200 mt-1">
            {format(new Date(reservation.gameDateTime), 'yyyy년 M월 d일 (E) HH:mm', { locale: ko })}
          </div>
        </div>

        {/* QR 코드 */}
        <div className="flex justify-center p-6 bg-gray-50">
          <QRCodeSVG value={reservation.qrCode} size={180} />
        </div>

        {/* 예매 정보 */}
        <div className="p-5 space-y-3 text-sm">
          <div className="flex justify-between">
            <span className="text-gray-500">경기장</span>
            <span>{reservation.stadium}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-500">좌석</span>
            <div className="text-right">
              {reservation.seats.map((s) => (
                <div key={s.seatId}>{s.sectionName} {s.row}열 {s.number}번</div>
              ))}
            </div>
          </div>
          <div className="flex justify-between border-t pt-3 font-bold">
            <span>결제 금액</span>
            <span className="text-blue-600">{reservation.totalPrice.toLocaleString()}원</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-500">예매번호</span>
            <span className="font-mono text-xs">{reservation.qrCode.slice(0, 13).toUpperCase()}</span>
          </div>
        </div>
      </div>
    </div>
  )
}

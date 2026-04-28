import { useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { useMutation } from '@tanstack/react-query'
import { reservationsApi } from '../api/reservations'

const PAYMENT_METHODS = [
  { value: 'CARD', label: '신용/체크카드' },
  { value: 'KAKAO_PAY', label: '카카오페이' },
  { value: 'NAVER_PAY', label: '네이버페이' },
  { value: 'TOSS', label: '토스' },
]

export default function CheckoutPage() {
  const { state } = useLocation()
  const navigate = useNavigate()
  const [method, setMethod] = useState('CARD')
  const [error, setError] = useState('')

  const { gameId, seatIds, sectionName, price } = state as {
    gameId: number
    seatIds: number[]
    sectionName: string
    price: number
  }

  const reserveMutation = useMutation({
    mutationFn: () => reservationsApi.create({ gameId, seatIds }),
    onSuccess: (reservation) => {
      payMutation.mutate({ reservationId: reservation.id, method })
    },
    onError: (err: any) => {
      setError(err.response?.data?.message || '예매 생성에 실패했습니다.')
    },
  })

  const payMutation = useMutation({
    mutationFn: (data: { reservationId: number; method: string }) =>
      reservationsApi.pay(data),
    onSuccess: (_, variables) => {
      navigate(`/reservations/${variables.reservationId}`)
    },
    onError: (err: any) => {
      setError(err.response?.data?.message || '결제에 실패했습니다.')
    },
  })

  const handlePayment = () => {
    setError('')
    reserveMutation.mutate()
  }

  const isProcessing = reserveMutation.isPending || payMutation.isPending

  return (
    <div className="max-w-lg mx-auto">
      <h1 className="text-2xl font-bold mb-6">결제</h1>

      <div className="bg-white rounded-xl shadow-sm border p-6 mb-4">
        <h2 className="font-semibold text-gray-700 mb-3">예매 정보</h2>
        <div className="space-y-2 text-sm">
          <div className="flex justify-between">
            <span className="text-gray-500">구역</span>
            <span>{sectionName}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-500">좌석 수</span>
            <span>{seatIds.length}석</span>
          </div>
          <div className="flex justify-between font-bold text-base mt-2 pt-2 border-t">
            <span>총 금액</span>
            <span className="text-blue-600">{price.toLocaleString()}원</span>
          </div>
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm border p-6 mb-6">
        <h2 className="font-semibold text-gray-700 mb-3">결제 방법</h2>
        <div className="grid grid-cols-2 gap-2">
          {PAYMENT_METHODS.map((m) => (
            <button
              key={m.value}
              onClick={() => setMethod(m.value)}
              className={`py-3 rounded-lg border text-sm font-medium transition ${
                method === m.value
                  ? 'border-blue-500 bg-blue-50 text-blue-700'
                  : 'border-gray-200 hover:border-blue-300'
              }`}
            >
              {m.label}
            </button>
          ))}
        </div>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-600 rounded-lg p-3 mb-4 text-sm">
          {error}
        </div>
      )}

      <button
        onClick={handlePayment}
        disabled={isProcessing}
        className="w-full bg-blue-600 text-white py-4 rounded-xl font-bold text-lg hover:bg-blue-700 disabled:opacity-50 transition"
      >
        {isProcessing ? '결제 처리 중...' : `${price.toLocaleString()}원 결제하기`}
      </button>

      <p className="text-xs text-gray-400 text-center mt-3">
        * 임시 점유 시간은 5분입니다. 시간 내 결제를 완료해 주세요.
      </p>
    </div>
  )
}

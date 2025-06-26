import { useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import Button from '~/components/button/Button'
import CustomerInfo from './component/CustomerInfo'
import OrderSummary from './component/OrderSummary'
import DiscountSection from './component/DiscountSection'
import PaymentMethod from './component/PaymentMethod'
import { useAppSelector } from '~/hooks/redux'
import { ShippingAddress } from './component/ShippingAddress'
import { OrderDetails, UserInfoOrder } from '~/types/order'
import { SelectedPromotion } from '~/types/promotion'
import { apiCreateOrderWithNoPayment, apiCreateOrderWithPayment } from '~/api/orders'
import { showAlertError, showAlertSucess, showToastError } from '~/utils/alert'
import { AxiosError } from 'axios'
import { Address } from '~/types/address'

export default function PaymentConfirmation() {
  const { state } = useLocation()
  const navigate = useNavigate()
  const [selectedPayment, setSelectedPayment] = useState('COD')
  const [selectedCoupon, setSelectdCoupon] = useState<SelectedPromotion | null>(null)
  const [discountAmount, setDiscountAmount] = useState(0)
  const { accessToken, isLoggedIn, userData } = useAppSelector((state) => state.user)
  const [selectedShippingInfo, setSelectedShippingInfo] = useState<Address | null>(null)
  const [userInfo, setUserInfo] = useState<UserInfoOrder>({} as UserInfoOrder)
  const [order, setOrder] = useState<OrderDetails>(state.order)
  const handleCreateOrder = async ({
    order,
    address,
    note,
    userPromotionId,
    accessToken
  }: {
    order: OrderDetails
    address: Address
    note: string | null
    userPromotionId: string | null
    accessToken: string
  }) => {
    const orderDetails = order.items.map((item) => ({
      productDetailId: item.productDetailId,
      quantity: item.quantity,
      productPromotionId: item.productPromotion?.length > 0 ? item.productPromotion[0].productPromotionId : null
    }))

    // return
    try {
      if (!accessToken) {
        showAlertError('Please login to continue')
      }

      const addressId = address.addressId

      if (!addressId) {
        showAlertError('Please fill in all required fields')
        return
      }
      console.log(selectedPayment)
      if (selectedPayment === 'COD') {
        const res = await apiCreateOrderWithNoPayment({
          orderDetails,
          addressId,
          note,
          userPromotionId,
          amount: order.totalAmount,
          accessToken
        })
        if (res.code === 200) {
          showAlertSucess('Order created successfully')
        } else {
          showToastError(res.message)
        }
      } else {
        const res = await apiCreateOrderWithPayment({
          orderDetails,
          addressId,
          note,
          userPromotionId,
          amount: order.totalAmount,
          accessToken
        })
        if (res.code === 200) {
          window.location.href = res.result.payUrl
        } else {
          showToastError(res.message)
        }
      }
    } catch (error) {
      if (error instanceof Error || error instanceof AxiosError) {
        showToastError(error.message)
      }
    }
  }

  useEffect(() => {
    if (!isLoggedIn || !accessToken) {
      navigate('/')
      return
    }
    setOrder(state.order)

    setUserInfo({
      name: `${userData?.firstName ?? ''} ${userData?.lastName ?? ''}`.trim(),
      phone: userData?.phone || '',
      email: userData?.email || '',
      note: null
    })
  }, [state.order])
  return (
    <div className='max-w-3xl mx-auto p-8 bg-white rounded-lg shadow-lg mt-16'>
      <h1 className='text-3xl font-bold text-blue-800 text-center'>Order Confirmation</h1>
      {userData && <ShippingAddress addresses={userData.addresses} setSelectedShippingInfo={setSelectedShippingInfo} />}
      <CustomerInfo setUserInfo={setUserInfo} userInfo={userInfo} selectedShippingInfo={selectedShippingInfo} />
      <OrderSummary order={order} discountAmount={discountAmount} />
      <DiscountSection
        orderTotal={order.totalAmount}
        selectedCoupon={selectedCoupon}
        setSelectdCoupon={setSelectdCoupon}
        setDiscountAmount={setDiscountAmount}
      />
      <PaymentMethod selectedPayment={selectedPayment} setSelectedPayment={setSelectedPayment} />
      <div className='mt-6 text-center'>
        <Button
          className='bg-blue-500 hover:bg-blue-600 text-white px-6 py-3 rounded-lg'
          onClick={() =>
            handleCreateOrder({
              order: order,
              note: userInfo.note,
              address: selectedShippingInfo || ({} as Address),
              userPromotionId: selectedCoupon?.promotionId || null,
              accessToken: accessToken || ''
            })
          }
        >
          Payment
        </Button>
      </div>
    </div>
  )
}

package com.uberando.hipasar.util

object Constants {

  const val DATABASE_NAME = "hisijang-android"

  /**
   * State code
   */
  const val SUCCESS = 1
  const val FAILED = 0
  const val CANCELED = 2

  /**
   * Server response code
   */
  const val CODE_UNAUTHORIZED = 401
  const val CODE_NOT_FOUND = 404
  const val CODE_INTERNAL_SERVER_ERROR = 500
  const val CODE_VERIFICATION_NOT_FOUND = 501
  const val CODE_VERIFICATION_EXPIRED = 502
  const val CODE_VERIFICATION_OK = 200
  const val CODE_PHONE_NOT_VERIFIED = 400
  const val CODE_DUPLICATE_ENTRY = "ER_DUP_ENTRY"

  /**
   * Activity with result request code
   */
  const val PAYMENT_REQUEST_CODE = 2
  const val AUTHENTICATION_REQUEST_CODE = 4
  const val GOOGLE_SIGN_IN_REQUEST_CODE = 100
  const val DAUM_REQUEST_CODE = 1
  const val ORDER_REQUEST_CODE = 3
  const val ADDRESS_REQUEST_CODE = 5

  /**
   * Intent extra key
   */
  const val INTENT_EXTRA_RESULT_STATE = "result_state"
  const val INTENT_EXTRA_REQUEST = "request_code"
  const val INTENT_EXTRA_PAYMENT_CALLBACK = "payment_callback"
  const val INTENT_EXTRA_URL = "browser_url"
  const val INTENT_EXTRA_ARGUMENTS = "arguments"
  const val INTENT_EXTRA_ADDRESS_RESULT = "address_result"
  const val INTENT_EXTRA_PURPOSE = "purpose"
  const val INTENT_EXTRA_ADDRESS = "address"

  /**
   * Regex expression
   */
  const val PATTERN_ALPHANUMERIC = "^(?=.*[a-z])(?=.*[0-9])[a-zA-Z0-9#?!@\$%^&*]+\$"

  /**
   * Data binding [TextInputLayout] target key to determine validation
   */
  const val INPUT_TARGET_NAME = "name"
  const val INPUT_TARGET_EMAIL = "email"
  const val INPUT_TARGET_PASSWORD = "password"
  const val INPUT_TARGET_PASSWORD_CONFIRM = "password-confirm"
  const val INPUT_TARGET_USERNAME = "username"
  const val INPUT_TARGET_PHONE = "phone"

  /**
   * Authentication input rules
   */
  const val MINIMUM_NAME_LENGTH = 2
  const val MINIMUM_USERNAME_LENGTH = 2
  const val MINIMUM_PASSWORD_LENGTH = 6

  /**
   * Authentication method
   */
  const val AUTH_METHOD_BASIC = "basic"
  const val AUTH_METHOD_GOOGLE = "google"
  const val AUTH_METHOD_KAKAO = "kakao"
  
  /**
   * Auth visiting context
   */
  const val AUTH_VISITING_CONTEXT_SIGN_IN = "sign-in"
  const val AUTH_VISITING_CONTEXT_REGISTER = "register"
  const val AUTH_VISITING_CONTEXT_VERIFY_EMAIL = "verify-email"

  const val CALLBACK_SUCCESS = "success"
  const val CALLBACK_FAILED = "failed"
  const val CALLBACK_CANCEL = "cancel"
  
  /**
   * Order status
   */
  const val ORDER_TYPE_ALL = -1
  const val ORDER_STATUS_PENDING = 0
  const val ORDER_STATUS_ADMIN_CONFIRMATION = 1
  const val ORDER_STATUS_ON_PROCESS = 2
  const val ORDER_STATUS_ON_DELIVERY = 3
  const val ORDER_STATUS_ARRIVED = 4
  const val ORDER_STATUS_FINISHED = 5
  const val ORDER_STATUS_CANCELLATION_REQUEST = 6
  const val ORDER_STATUS_CANCELLED = 7
  const val ORDER_STATUS_PAYMENT_FAILED = 8

  const val ADDRESS_PRIMARY_TYPE = 1
  const val ADDRESS_NON_PRIMARY_TYPE = 2
  
  const val VISITING_CONTEXT_CREATE = 1
  const val VISITING_CONTEXT_MODIFY = 2

  const val KEY_TRANSACTION_FINISHED = "tfd"
  const val KEY_TRANSACTION_FAILED = "tfl"
  const val KEY_GET_ADDRESS = "kga"
  const val KEY_BROWSER_VISIT_PURPOSE = "bfp"
  const val KEY_MODIFY_ADDRESS = "kma"
  const val KEY_ORDER_ID = "oi"
  const val KEY_ORDER_PRICE = "op"
  const val KEY_ORDER_CODE = "oc"
  const val KEY_ORDER_MODIFIED = "om"
  const val KEY_CREATE_ADDRESS = "kca"
  const val KEY_GET_PAYMENT_METHOD =" gpm"
  const val KEY_GET_LOCATION = "gl"
  const val KEY_GET_DELIVERY_TIME = "gdt"
  const val KEY_GET_DELIVERY_DATE = "gdd"

  const val PURPOSE_DEFAULT = "default"
  const val PURPOSE_GET_ADDRESS = "get-address"
  const val PURPOSE_PAYMENT = "payment"
  const val PURPOSE_PRIVACY_POLICY = "privacy-policy"

  /**
   * Server payment type
   */
  const val PAYMENT_TYPE_EWALLET = "EWALLET"
  const val PAYMENT_TYPE_VA = "VA"
  const val PAYMENT_TYPE_COD = "COD"

  /**
   * Wallet type
   */
  const val WALLET_TYPE_DANA = "DANA"
  const val WALLET_TYPE_SHOPEEPAY = "SHOPEEPAY"
  const val WALLET_TYPE_LINKAJA = "LINKAJA"

  const val DURIAN_PAY_DANA_RETURN_URL = "https://checkout.durianpay.id/dana/return_url"

}
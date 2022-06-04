package com.uberando.hipasar.di

import com.uberando.hipasar.BuildConfig
import com.uberando.hipasar.data.source.remote.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

  @Provides
  @Singleton
  fun provideOkHttpClient(): OkHttpClient {
     val loggingInterceptor = HttpLoggingInterceptor()
       .apply { level = HttpLoggingInterceptor.Level.BODY }
    return OkHttpClient.Builder()
      .addInterceptor(loggingInterceptor)
      .build()
  }

  @OtherApi
  @Provides
  @Singleton
  fun provideOkHttpIndonesiaApiClient(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor()
      .apply { level = HttpLoggingInterceptor.Level.BODY }
    return OkHttpClient.Builder()
      .addInterceptor(loggingInterceptor)
      .build()
  }

  @Provides
  @Singleton
  fun provideRetrofit(client: OkHttpClient): Retrofit =
    Retrofit.Builder()
      .baseUrl(BuildConfig.BASE_API_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .client(client)
      .build()

  @OtherApi
  @Provides
  @Singleton
  fun provideIndonesianApi(@OtherApi client: OkHttpClient): Retrofit =
    Retrofit.Builder()
      .baseUrl(BuildConfig.BASE_INDONESIAN_ADDRESS_API_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .client(client)
      .build()

  @Provides
  fun provideAuthService(retrofit: Retrofit): AuthService =
    retrofit.create(AuthService::class.java)

  @Provides
  fun provideAccountService(retrofit: Retrofit): AccountService =
    retrofit.create(AccountService::class.java)

  @Provides
  fun provideFileService(retrofit: Retrofit): FileService =
    retrofit.create(FileService::class.java)

  @Provides
  fun provideProductService(retrofit: Retrofit): ProductService =
    retrofit.create(ProductService::class.java)

  @Provides
  fun provideOrderService(retrofit: Retrofit): OrderService =
    retrofit.create(OrderService::class.java)

  @Provides
  fun provideAddressService(retrofit: Retrofit): AddressService =
    retrofit.create(AddressService::class.java)
  
  @Provides
  fun provideCheckoutService(retrofit: Retrofit): CheckoutService =
    retrofit.create(CheckoutService::class.java)

  @Provides
  fun providePaymentService(retrofit: Retrofit): PaymentService =
    retrofit.create(PaymentService::class.java)

  @OtherApi
  @Provides
  fun provideIndonesianAddressService(@OtherApi retrofit: Retrofit): IndonesianAddressService =
    retrofit.create(IndonesianAddressService::class.java)

  @Provides
  fun provideCartService(retrofit: Retrofit): CartService =
    retrofit.create(CartService::class.java)

  @Provides
  fun provideBannerService(retrofit: Retrofit): BannerService =
    retrofit.create(BannerService::class.java)

  @Provides
  fun provideMessagingService(retrofit: Retrofit): MessagingService =
    retrofit.create(MessagingService::class.java)

}
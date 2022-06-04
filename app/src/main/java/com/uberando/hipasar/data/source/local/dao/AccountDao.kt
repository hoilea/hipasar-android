package com.uberando.hipasar.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.uberando.hipasar.data.source.local.entity.AccountEntity

@Dao
interface AccountDao : BaseDao<AccountEntity> {

  @Query("SELECT * FROM account LIMIT 1")
  suspend fun getSingleAccount(): AccountEntity?

  @Query("DELETE FROM account")
  suspend fun clearAllAccount()

}
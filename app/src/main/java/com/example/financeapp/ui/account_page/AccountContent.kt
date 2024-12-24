package com.example.financeapp.ui.account_page


import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeapp.models.requests.UpdateUserRequest
import com.example.financeapp.models.responses.UserDataResponse
import com.example.financeapp.services.RetrofitClient
import com.example.financeapp.viewmodel.UserViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@OptIn(ExperimentalMaterial3Api::class)
fun AccountContent(
    userViewModel: UserViewModel
): @Composable () -> Unit {

    val infoRowModifier = Modifier
        .fillMaxWidth()
        .height(40.dp)

    val content = @Composable{
        Box() {
            val context = LocalContext.current
            val token by userViewModel.token.observeAsState()
            val apiService = RetrofitClient.apiService

            val user = remember {
                mutableStateOf(
                    UserDataResponse(
                        user = UserDataResponse.UpdatedUser(
                            name = "",
                            email = "",
                            currency = "",
                            referalCode = "",
                            role = ""
                        )
                    )
                )
            }

            fun showMessageToUser(message: String) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

            fun editName(newName: String) {
                val request = UpdateUserRequest(
                    name = newName,
                    currency = user.value.user.currency,
                    role = user.value.user.role
                )

                val call = apiService.updateUserData("Bearer $token", request)
                call.enqueue(object : Callback<UserDataResponse> {
                    override fun onResponse(
                        call: Call<UserDataResponse>,
                        response: Response<UserDataResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let { responseBody ->
                                user.value = responseBody
                                showMessageToUser("Name updated successfully!")
                            }
                        } else {
                            showMessageToUser("Error: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<UserDataResponse>, t: Throwable) {
                        showMessageToUser("Error: ${t.localizedMessage}")
                    }
                })
            }

            fun editCurrency(newCurrency: String){
                val request = UpdateUserRequest(
                    name = user.value.user.name,
                    currency = newCurrency,
                    role = user.value.user.role
                )

                val call = apiService.updateUserData("Bearer $token", request)
                call.enqueue(object : Callback<UserDataResponse> {
                    override fun onResponse(
                        call: Call<UserDataResponse>,
                        response: Response<UserDataResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let { responseBody ->
                                user.value = responseBody
                                showMessageToUser("Currency updated successfully!")
                            }
                        } else {
                            showMessageToUser("Error: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<UserDataResponse>, t: Throwable) {
                        showMessageToUser("Error: ${t.localizedMessage}")
                    }
                })
            }

            fun logOut(){
                val call = apiService.logoutUser("Bearer $token")
                call.enqueue(object : Callback<Void> {
                    override fun onResponse(
                        call: Call<Void>,
                        response: Response<Void>
                    ) {
                        if (response.isSuccessful) {
                            userViewModel.setToken(" ")
                            showMessageToUser("User logouted successfully!")
                        } else {
                            showMessageToUser("Error: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        showMessageToUser("Error: ${t.localizedMessage}")
                    }
                })
            }

            fun delete(){
                val call = apiService.deleteAccount("Bearer $token")
                call.enqueue(object : Callback<Void> {
                    override fun onResponse(
                        call: Call<Void>,
                        response: Response<Void>
                    ) {
                        if (response.isSuccessful) {
                            userViewModel.setToken(" ")
                            showMessageToUser("Account deleted successfully!")
                        } else {
                            showMessageToUser("Error: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        showMessageToUser("Error: ${t.localizedMessage}")
                    }
                })
            }

            if( token != null ) {
                DisposableEffect(Unit) {
                    val call = apiService.getUserData("Bearer $token")
                    call.enqueue(object : Callback<UserDataResponse> {
                        override fun onResponse(
                            call: Call<UserDataResponse>,
                            response: Response<UserDataResponse>
                        ) {
                            if (response.isSuccessful) {
                                response.body()?.let { responseBody ->
                                    user.value = responseBody
                                }
                            } else {
                                showMessageToUser("Error: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<UserDataResponse>, t: Throwable) {
                            showMessageToUser("Error: ${t.localizedMessage}")
                        }
                    })
                    onDispose {
                        call.cancel()
                    }
                }

                Column(
                    modifier = Modifier.padding(40.dp, 100.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    Text(
                        text = "Акаунт",
                        textAlign = TextAlign.Center,
                        fontSize = 32.sp,
                        modifier = Modifier
                            .padding(0.dp, 20.dp)
                            .fillMaxWidth()
                    )
                    Column {
                        Row(
                            modifier = infoRowModifier,
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(text = "Ім'я")
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(text = user.value.user.name)
                                IconButton(onClick = { editName("new name") }) {
                                    Icon(Icons.Filled.Edit, "Edit name")
                                }
                            }
                        }
                        HorizontalDivider(color = Color(0xFF222831))

                        Row(
                            modifier = infoRowModifier,
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(text = "Пошта")
                            Text(text = user.value.user.email)
                        }
                        HorizontalDivider(color = Color(0xFF222831))

                        Row(
                            modifier = infoRowModifier,
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(text = "Основна валюта")
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(text = user.value.user.currency)
                                if(user.value.user.role == "admin") {
                                    IconButton(onClick = { editCurrency("UAH") }) {
                                        Icon(Icons.Filled.Edit, "Edit currency")
                                    }
                                }
                            }
                        }
                        HorizontalDivider(color = Color(0xFF222831))

                        Row(
                            modifier = infoRowModifier,
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(text = "Реферальний код")
                            Text(text = user.value.user.referalCode)
                        }
                        HorizontalDivider(color = Color(0xFF222831))

                        Row(
                            modifier = infoRowModifier,
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(text = "Роль у групі")
                            Text(text = user.value.user.role)
                        }
                        HorizontalDivider(color = Color(0xFF222831))
                    }

                    OutlinedButton(
                        modifier = Modifier
                            .absoluteOffset(y = 390.dp)
                            .fillMaxWidth()
                            .border(
                                2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(50)
                            ),
                        onClick = { logOut() },
                        border = ButtonDefaults.outlinedButtonBorder(false)
                    ) {
                        Text(text = "Вийти з облікового запису")
                    }

                    OutlinedButton(
                        modifier = Modifier
                            .absoluteOffset(y = 390.dp)
                            .fillMaxWidth()
                            .border(
                                2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(50)
                            ),
                        onClick = { delete() },
                        border = ButtonDefaults.outlinedButtonBorder(false)
                    ) {
                        Text(text = "Видалити акаунт")
                    }
                }
            }
        }
    }

    return content

}
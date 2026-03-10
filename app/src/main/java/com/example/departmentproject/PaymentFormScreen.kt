package com.example.departmentproject

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentFormScreen(
    amountFromBill: String,
    month: String,
    year: String,
    userId: Int,
    navController: NavHostController
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var amount by remember { mutableStateOf(amountFromBill) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = { Text("ชำระเงินค่าเช่าหอ") },

                navigationIcon = {

                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back"
                        )

                    }

                }

            )

        }

    ) { padding ->

        Column(

            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()

        ) {

            Card(

                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F0FE)
                ),
                modifier = Modifier.fillMaxWidth()

            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "ยอดที่ต้องชำระ",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "$amountFromBill บาท",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("เดือน $month / ปี $year")

                }

            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(

                value = amount,
                onValueChange = { amount = it },
                label = { Text("จำนวนเงินที่โอน") },
                modifier = Modifier.fillMaxWidth()

            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "แนบสลิปการโอน",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(
                        2.dp,
                        Color.Gray,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable {

                        launcher.launch("image/*")

                    },

                contentAlignment = Alignment.Center

            ) {

                if (imageUri != null) {

                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )

                } else {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = null
                        )

                        Text("แตะเพื่อเลือกสลิป")

                    }

                }

            }

            Spacer(modifier = Modifier.weight(1f))

            Button(

                onClick = {

                    if (imageUri == null) {

                        Toast.makeText(
                            context,
                            "กรุณาแนบสลิปก่อน",
                            Toast.LENGTH_LONG
                        ).show()

                        return@Button

                    }

                    scope.launch {

                        try {

                            val inputStream =
                                context.contentResolver.openInputStream(imageUri!!)

                            val file =
                                File(context.cacheDir, "slip.jpg")

                            inputStream!!.use { input ->
                                file.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }

                            val requestFile =
                                file.readBytes()
                                    .toRequestBody("image/*".toMediaType())

                            val slipPart =
                                MultipartBody.Part.createFormData(
                                    "slip",
                                    file.name,
                                    requestFile
                                )

                            val amountBody =
                                amount.toRequestBody("text/plain".toMediaType())

                            val monthBody =
                                month.toRequestBody("text/plain".toMediaType())

                            val yearBody =
                                year.toRequestBody("text/plain".toMediaType())

                            val userBody =
                                userId.toString()
                                    .toRequestBody("text/plain".toMediaType())

                            val response =
                                RetrofitInstance.api.uploadSlip(
                                    slipPart,
                                    amountBody,
                                    monthBody,
                                    yearBody,
                                    userBody
                                )

                            if (response.isSuccessful) {

                                Toast.makeText(
                                    context,
                                    "ชำระเงินสำเร็จ",
                                    Toast.LENGTH_LONG
                                ).show()

                                navController.navigate(Screen.Finance.route) {
                                    popUpTo(Screen.Finance.route)
                                    launchSingleTop = true
                                }

                            }

                        } catch (e: Exception) {

                            Toast.makeText(
                                context,
                                e.message,
                                Toast.LENGTH_LONG
                            ).show()

                        }

                    }

                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),

                shape = RoundedCornerShape(14.dp)

            ) {

                Text("ยืนยันการชำระ")

            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(

                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterHorizontally)

            ) {

                Text("ยกเลิก")

            }

        }

    }

}
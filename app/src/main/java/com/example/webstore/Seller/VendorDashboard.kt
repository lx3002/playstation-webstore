package com.example.webstore.Seller


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.webstore.MainActivity

import com.example.webstore.Seller.model.ProductsObj
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class VendorDashboard : ComponentActivity() {
    private lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            com.example.webstore.ui.theme.WebStoreTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(topBar = {
                        TopAppBar(backgroundColor = Color.Black,
                            title = {
                                Text(
                                    text = "Product signup",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                            })
                    }) {
                        Column(modifier = Modifier.padding(it)) {

                            val firebaseDatabase = FirebaseDatabase.getInstance()
                            val databaseReference = firebaseDatabase.getReference("ProductDB")

                            val storage  = Firebase.storage
                            storageReference = storage.reference.child("ProductImages")
                            productForm(LocalContext.current,databaseReference,storageReference)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun productForm(
    context: Context,
    databaseReference: DatabaseReference,
    storageReference: StorageReference
){
    //variables to store the users input
    val productname = remember{ mutableStateOf(TextFieldValue()) }
    val productcontact = remember{ mutableStateOf(TextFieldValue()) }
    val productprice = remember{ mutableStateOf(TextFieldValue()) }


    Column(modifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth()
        .background(Color.White),
        verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to the signup page",modifier = Modifier.padding(7.dp), style= TextStyle(
            color = Color.Black, fontSize = 10.sp
        ), fontWeight = FontWeight.Bold)


        OutlinedTextField(value = productname.value, onValueChange = {productname.value = it},
            placeholder = { Text(text = "Enter the Product Name", color = Color.Black)}, modifier = Modifier
                .background(color = Color.LightGray)
                .padding(10.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
             singleLine = true,
            leadingIcon = null
        )
        Spacer(modifier = Modifier.height(5.dp))
        OutlinedTextField(value = productcontact.value, onValueChange = {productcontact.value = it},
            placeholder = { Text(text = "Enter the Product Contact", color = Color.Black)}, modifier = Modifier
                .background(color = Color.LightGray)
                .padding(10.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true,
            leadingIcon = null
        )
        Spacer(modifier = Modifier.height(5.dp))



        val selectedUri = remember { mutableStateOf<Uri?>(null) }

        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()){

            selectedUri.value = it
        }

        var isLoading by remember {
            mutableStateOf(false)
        }


        Button(onClick = {
            

            launcher.launch("image/*")
        }) {
          Text(text = "Upload image", color = Color.Black)




        }

        Spacer(modifier = Modifier.height(5.dp))
        OutlinedTextField(value = productprice.value, onValueChange = {productprice.value = it},
            placeholder = { Text(text = "Enter the Product Price", color = Color.Black)}, modifier = Modifier
                .background(color = Color.LightGray)
                .padding(10.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            leadingIcon = null,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
                 isLoading = true
                 isLoading= false
            selectedUri.value?.let{
                val imageName = "image_${System.currentTimeMillis()}"
                val imageRef = storageReference.child(imageName)
                imageRef.putFile(it).addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener {
                        // after getting our download url save the details to real time database
                        // use the push method to generate a unique key for the record
                        // converting the storage download url to a string
                        val imagePath = it.toString()
                        val newProduct_Reference = databaseReference.push()
                        // key / unique identifier
                        val productId = newProduct_Reference.key
                        val productObj = productId?.let {
                            ProductsObj(
                                it,productname.value.text,productcontact.value.text,imagePath,
                                productprice.value.text)
                        }

                        // we use a class in firebase called the addValueEventListener
                        databaseReference.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                newProduct_Reference.setValue(productObj)
                                Toast.makeText(context,"Product has been added successfully!!,", Toast.LENGTH_LONG).show()
                                Log.d("Product Push",snapshot.toString())
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                                Toast.makeText(context,"Product failed to be  added!!,", Toast.LENGTH_LONG).show()
                                Log.d("Product Push",error.message)
                            }

                        })
                    }
                }
            }



        }, modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), enabled = selectedUri.value != null) {
            if (isLoading){
                //LoadingProgress()
//                        CircularProgressIndicator()
            } else {
                Text(text = "Add Product Details", color = Color.Black)
            }


        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround){

            val activitylauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()){ activityResult ->

            }
            Button(colors = ButtonDefaults.buttonColors(backgroundColor = Color.Yellow),
                onClick = {
                    val intent = Intent(context,ViewInventory::class.java)
                    activitylauncher.launch(intent)
                }) {

                Text(text = "View Inventory", modifier = Modifier.padding(5.dp), color = Color.Black)
            }

            //logout

            Button(colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    changeUi(context,activitylauncher)
                }) {
                Text(text = "Logout", modifier = Modifier.padding(5.dp))
            }
        }
    }
}

fun changeUi(
    context: Context,
    activitylauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    val intent = Intent(context,MainActivity::class.java)
    activitylauncher.launch(intent)
}

@Composable
fun imageUploader(activity: ComponentActivity, storageReference: StorageReference, databaseReference: DatabaseReference) {
    // state to hold image uri
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    //activity result launcher to start image picker
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()){
        selectedImageUri.value = it
    }

    Column() {
        //button to launch the image picker
        Button(onClick = {
            launcher.launch("image/*")
        }) {
            Text(text = "select product image")
        }
    }
}















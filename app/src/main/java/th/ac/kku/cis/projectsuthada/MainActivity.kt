@file:OptIn(ExperimentalMaterial3Api::class)

package th.ac.kku.cis.projectsuthada

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import th.ac.kku.cis.projectsuthada.ui.theme.ProjectsuthadaTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectsuthadaTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "inputScreen") {
                    composable("inputScreen") {
                        InputScreen(navController)
                    }
                    composable("loginScreen") {
                        LoginScreen(navController)
                    }
                    composable("displayScreen0/{email}/{password}") { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        val password = backStackEntry.arguments?.getString("password") ?: ""
                        DisplayScreen0(navController)
                    }
                    composable("displayScreen0") {
                        DisplayScreen0(navController)
                    }
                    composable("displayScreen1") {
                        DisplayScreen1(navController)
                    }
                }
            }
        }
    }
}

//register
@Composable
fun InputScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.padding(8.dp)
            )

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.padding(8.dp)
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.padding(8.dp)
            )

            Button(
                onClick = {
                    saveDataToFirestore(name, email, password)
                    navController.navigate("loginScreen") // Navigate to the login screen
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Save")
            }
            Button(
                onClick = {
                    navController.navigate("loginScreen") // Navigate to the login screen
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Login")
            }
        }
    }
}

//save register
fun saveDataToFirestore(name: String, email: String, password: String) {
    val db = FirebaseFirestore.getInstance()
    val data = hashMapOf(
        "name" to name,
        "email" to email,
        "password" to password
    )
    db.collection("register") // Replace with your actual collection name
        .add(data)
        .addOnSuccessListener { documentReference ->
            Log.d("MainActivity", "DocumentSnapshot added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            Log.w("MainActivity", "Error adding document", e)
        }
}

@Composable
//login
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.padding(8.dp)
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.padding(8.dp)
            )

            Button(
                onClick = {
                    navController.navigate("displayScreen0/$email/$password")
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Login")
            }
        }
    }
}

//แสดงเกี่ยวกับรายละเอียดของข้อมูล
@Composable
fun DisplayScreen0(navController: NavHostController) {
    val dataFoodDocuments = remember { mutableStateListOf<DocumentSnapshot>() }
    val selectedDocument = remember { mutableStateOf<DocumentSnapshot?>(null) }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("datafood")
            .get()
            .addOnSuccessListener { querySnapshot ->
                dataFoodDocuments.addAll(querySnapshot.documents)
            }
            .addOnFailureListener { exception ->
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            dataFoodDocuments.forEach { document ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            selectedDocument.value = document
                        },
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Image(
                            painter = rememberImagePainter(data = document.getString("imageLink")),
                            contentDescription = null,
                            modifier = Modifier
                                .size(128.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .align(Alignment.CenterHorizontally)
                        )
                        Text("Name: ${document.getString("name")}", fontWeight = FontWeight.Bold)
                        Text("Serving Count: ${document.getLong("servingCount")}")
                        Text("Cooking Time: ${document.getLong("cookingTime")} minutes")
                        Text("Ingredients:")
                        val ingredientsList = document.get("ingredientsList") as? List<String>
                        ingredientsList?.forEach { ingredient ->
                            Text(" - $ingredient")
                        }
                        Text("Steps:")
                        val stepsList = document.get("stepsList") as? List<String>
                        stepsList?.forEachIndexed { index, step ->
                            Text("${index + 1}. $step")
                        }
                    }
                }
            }
            Button(
                onClick = { navController.navigate("displayScreen1") },
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Add Information")
            }
        }
    }

    selectedDocument.value?.let { document ->
        EditScreen(document = document) {
            selectedDocument.value = null
        }
    }
}

//แก้ไข
@Composable
fun EditScreen(document: DocumentSnapshot, onClose: () -> Unit) {
    var newName by remember { mutableStateOf(document.getString("name") ?: "") }
    var newServingCount by remember { mutableStateOf(document.getLong("servingCount")?.toString() ?: "") }
    var newCookingTime by remember { mutableStateOf(document.getLong("cookingTime")?.toString() ?: "") }
    var newIngredientsList by remember { mutableStateOf((document.get("ingredientsList") as? List<String>)?.joinToString("\n") ?: "") }
    var newStepsList by remember { mutableStateOf((document.get("stepsList") as? List<String>)?.joinToString("\n") ?: "") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = newServingCount,
                onValueChange = { newServingCount = it },
                label = { Text("Serving Count") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = newCookingTime,
                onValueChange = { newCookingTime = it },
                label = { Text("Cooking Time (minutes)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = newIngredientsList,
                onValueChange = { newIngredientsList = it },
                label = { Text("Ingredients") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = newStepsList,
                onValueChange = { newStepsList = it },
                label = { Text("Steps") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 10
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        val newData = mapOf(
                            "name" to newName,
                            "servingCount" to newServingCount.toLong(),
                            "cookingTime" to newCookingTime.toLong(),
                            "ingredientsList" to newIngredientsList.split("\n"),
                            "stepsList" to newStepsList.split("\n")
                        )
                        updateDocument(document.id, newData)
                        onClose()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = onClose,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    deleteDocument(document.id)
                    onClose()
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("Delete")
            }
        }
    }
}

private val db = FirebaseFirestore.getInstance()

//อัพเดตข้อมูล
fun updateDocument(documentId: String, newData: Map<String, Any>) {
    db.collection("datafood")
        .document(documentId)
        .update(newData)
        .addOnSuccessListener {
        }
        .addOnFailureListener { exception ->
        }
}

//ลบข้อมูล
fun deleteDocument(documentId: String) {
    db.collection("datafood")
        .document(documentId)
        .delete()
        .addOnSuccessListener {
        }
        .addOnFailureListener { exception ->
        }
}

//เพิ่มสูตรอาหาร
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayScreen1(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var servingCount by remember { mutableStateOf(2) }
    var cookingTime by remember { mutableStateOf(30) }
    var imageLink by remember { mutableStateOf("") } // แทนที่ selectedImageUri ด้วย imageLink
    var ingredientsList by remember { mutableStateOf(listOf<String>()) }
    var stepsList by remember { mutableStateOf(listOf<String>()) }

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("เพิ่มสูตร") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                TextField(
                    value = imageLink,
                    onValueChange = { imageLink = it },
                    label = { Text("ใส่ลิงค์รูป") },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))



            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("ชื่อสูตร") },
                modifier = Modifier.fillMaxWidth()
            )}

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        // Decrement servingCount by 1
                        servingCount = if (servingCount > 0) servingCount - 1 else servingCount
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("-")
                }
                // Center button with serving count
                OutlinedButton(
                    onClick = { /*Optional: Add functionality for this button*/ },
                    modifier = Modifier
                        .weight(2f) // Make this button take up more space
                        .padding(horizontal = 8.dp) // Add padding for visual separation
                ) {
                    Text("สำหรับ $servingCount คน")
                }
                OutlinedButton(
                    onClick = {
                        // Increment servingCount by 1
                        servingCount = (servingCount + 1) % 10
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        // Decrement cookingTime by 10
                        cookingTime = if (cookingTime > 0) cookingTime - 10 else cookingTime
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("-")
                }
                // Center button with serving count
                OutlinedButton(
                    onClick = { /*No change needed*/ },
                    modifier = Modifier.weight(3f) // Adjusted weight for the center button
                ) {
                    Text("เวลาใช้ $cookingTime นาที")
                }
                OutlinedButton(
                    onClick = {
                        // Increment cookingTime by 10
                        cookingTime = (cookingTime + 10) % 180
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Text("ส่วนผสม", modifier = Modifier.padding(horizontal = 16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                ingredientsList.forEach { ingredient ->
                    TextField(
                        value = ingredient,
                        onValueChange = { newValue ->
                            ingredientsList = ingredientsList.toMutableList().apply {
                                set(ingredientsList.indexOf(ingredient), newValue)
                            }
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .width(200.dp), // กำหนดความกว้างตามต้องการ
                        singleLine = true
                    )
                }
            }
            Button(
                onClick = { ingredientsList = ingredientsList + "" },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text("เพิ่มส่วนผสม")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("วิธีทำ", modifier = Modifier.padding(horizontal = 16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                stepsList.forEach { step ->
                    TextField(
                        value = step,
                        onValueChange = { newValue ->
                            stepsList = stepsList.toMutableList().apply {
                                set(stepsList.indexOf(step), newValue)
                            }
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .width(200.dp), // กำหนดความกว้างตามต้องการ
                        singleLine = true
                    )
                }
            }
            Button(
                onClick = { stepsList = stepsList + "" },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text("เพิ่มขั้นตอน")
            }
            Row(
                modifier = Modifier
                    .width(400.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        saveDataToFirestore2(imageLink, name, servingCount, cookingTime, ingredientsList, stepsList, navController)
                        navController.navigate("displayScreen0")
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Save Data")
                }
            }
        }
    }
}

//บันทึกใน friebase
fun saveDataToFirestore2(
    imageLink: String,
    name: String,
    servingCount: Int,
    cookingTime: Int,
    ingredientsList: List<String>,
    stepsList: List<String>,
    navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    val data = hashMapOf(
        "imageLink" to imageLink,
        "name" to name,
        "servingCount" to servingCount,
        "cookingTime" to cookingTime,
        "ingredientsList" to ingredientsList,
        "stepsList" to stepsList
    )
    db.collection("datafood")
        .add(data)
        .addOnSuccessListener { documentReference ->
            Log.d("MainActivity", "DocumentSnapshot added with ID: ${documentReference.id}")
            // After successfully saving, navigate to the displayEnteredInformation route
            //navController.navigate("displayScreen0/${documentReference.id}")
        }
        .addOnFailureListener { e ->
            Log.w("MainActivity", "Error adding document", e)
        }
}


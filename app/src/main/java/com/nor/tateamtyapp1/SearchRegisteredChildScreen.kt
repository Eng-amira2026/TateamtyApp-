@file:OptIn(ExperimentalMaterial3Api::class)

package com.nor.tateamtyapp1

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val searchScreenBgColor = Color(0xFF1E6FAA)

@Composable
fun SearchRegisteredChildScreen(
    onBackPressed: () -> Unit,
    onViewRecordsClicked: (childId: String) -> Unit,
    searchViewModel: SearchChildViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchState = searchViewModel.searchState

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Search Records", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(painterResource(id = R.drawable.ic_arrowback), "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = searchScreenBgColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(searchScreenBgColor)
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 💡 اللمسة الإرشادية بالعربي
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("💡", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تنبيه: يمكنك البحث بكتابة اسم الطفل، أو الرقم الوطني المكون من 11 رقم، أو رقم هاتف ولي الأمر المسجل.",
                        color = Color.White,
                        fontSize = 20.sp, 
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp, 
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (searchViewModel.searchState !is SearchState.Idle) {
                        searchViewModel.resetState()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Name, National ID or Phone", color = Color.White.copy(alpha = 0.5f)) },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f)
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { searchViewModel.searchChild(searchQuery) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = searchScreenBgColor),
                enabled = searchQuery.isNotBlank() && searchState !is SearchState.Loading
            ) {
                if (searchState is SearchState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = searchScreenBgColor)
                } else {
                    Text("Search Now", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when (searchState) {
                    is SearchState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
                    }
                    is SearchState.Success -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text("Search Results (${searchState.results.size})", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            items(searchState.results) { child ->
                                SearchResultCard(
                                    child = child,
                                    onClick = { onViewRecordsClicked(child.id) }
                                )
                            }
                        }
                    }
                    is SearchState.NotFound -> {
                        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", fontSize = 50.sp)
                            Text("No matching records found.", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    is SearchState.Error -> {
                        Text("Error: ${searchState.message}", color = Color.Red, modifier = Modifier.align(Alignment.Center))
                    }
                    is SearchState.Idle -> {
                        Text("Waiting for search query...", color = Color.White.copy(alpha = 0.6f), modifier = Modifier.align(Alignment.TopCenter))
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(child: SearchedChildInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(searchScreenBgColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("👶", fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(child.name, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.Black)
                Text("National ID: ${child.nationalId}", fontSize = 12.sp, color = Color.Gray)
                Text("Phone: ${child.phone}", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(painterResource(id = android.R.drawable.ic_media_play), null, tint = searchScreenBgColor, modifier = Modifier.size(20.dp))
        }
    }
}

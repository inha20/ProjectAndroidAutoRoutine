package com.example.autoroutine.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.autoroutine.data.local.RoutineEntity
import com.example.autoroutine.presentation.viewmodel.RoutineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoRoutineScreen(
    viewModel: RoutineViewModel = viewModel()
) {
    val suggestedRoutines by viewModel.suggestedRoutines.collectAsState()
    val activeRoutines by viewModel.activeRoutines.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AutoRoutine AI") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // 제안된 루틴 영역 (사용자 승인 대기)
            if (suggestedRoutines.isNotEmpty()) {
                Text(
                    text = "새로운 제안이 있습니다 ✨",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                LazyColumn {
                    items(suggestedRoutines) { routine ->
                        RoutineSuggestionCard(
                            routine = routine,
                            onAccept = { viewModel.acceptRoutine(it) },
                            onReject = { viewModel.rejectRoutine(it) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 활성화된 내 루틴 영역
            Text(
                text = "내 루틴 (${activeRoutines.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn {
                items(activeRoutines) { routine ->
                    ActiveRoutineCard(routine)
                }
            }
        }
    }
}

@Composable
fun RoutineSuggestionCard(
    routine: RoutineEntity,
    onAccept: (RoutineEntity) -> Unit,
    onReject: (RoutineEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "💡 발견된 루틴: ${routine.ruleName}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "조건: ${routine.condition}")
            Text(text = "동작: ${routine.action}")
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { onReject(routine) }) {
                    Text("무시")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onAccept(routine) }) {
                    Text("루틴 만들기")
                }
            }
        }
    }
}

@Composable
fun ActiveRoutineCard(routine: RoutineEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = routine.ruleName, style = MaterialTheme.typography.titleSmall)
                Text(text = "조건: ${routine.condition}", style = MaterialTheme.typography.bodySmall)
            }
            Switch(
                checked = routine.isActive,
                onCheckedChange = { /* Toggle logic */ }
            )
        }
    }
}

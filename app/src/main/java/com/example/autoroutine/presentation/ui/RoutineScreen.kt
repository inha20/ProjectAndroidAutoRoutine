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
import com.example.autoroutine.presentation.state.RoutineUiState
import com.example.autoroutine.presentation.viewmodel.RoutineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoRoutineScreen(
    viewModel: RoutineViewModel = viewModel()
) {
    // 단 하나의 StateFlow만 관찰하여 전체 UI 분기 달성 (UDF)
    val uiState by viewModel.uiState.collectAsState()

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
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                is RoutineUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is RoutineUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is RoutineUiState.Success -> {
                    RoutineContent(
                        suggestedRoutines = state.suggestedRoutines,
                        activeRoutines = state.activeRoutines,
                        onAccept = { viewModel.acceptRoutine(it) },
                        onReject = { viewModel.rejectRoutine(it) },
                        onToggle = { routine, isChecked -> viewModel.toggleRoutine(routine, isChecked) }
                    )
                }
            }
        }
    }
}

@Composable
fun RoutineContent(
    suggestedRoutines: List<RoutineEntity>,
    activeRoutines: List<RoutineEntity>,
    onAccept: (RoutineEntity) -> Unit,
    onReject: (RoutineEntity) -> Unit,
    onToggle: (RoutineEntity, Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (suggestedRoutines.isNotEmpty()) {
            Text(
                text = "새로운 제안이 있습니다 ✨",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                items(suggestedRoutines) { routine ->
                    RoutineSuggestionCard(routine, onAccept, onReject)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (activeRoutines.isEmpty()) "활성화된 루틴이 없습니다." else "내 루틴 (${activeRoutines.size})",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(activeRoutines) { routine ->
                ActiveRoutineCard(routine, onToggle)
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
                TextButton(onClick = { onReject(routine) }) { Text("무시") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onAccept(routine) }) { Text("적용하기") }
            }
        }
    }
}

@Composable
fun ActiveRoutineCard(
    routine: RoutineEntity,
    onToggle: (RoutineEntity, Boolean) -> Unit
) {
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
                onCheckedChange = { isChecked -> onToggle(routine, isChecked) }
            )
        }
    }
}

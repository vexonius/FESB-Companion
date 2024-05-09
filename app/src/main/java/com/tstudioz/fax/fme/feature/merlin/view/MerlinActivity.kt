package com.tstudioz.fax.fme.feature.merlin.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel

@OptIn(InternalCoroutinesApi::class)
class MerlinActivity : AppCompatActivity() {

    private val merlinViewModel: MerlinViewModel by viewModel()

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        merlinViewModel.list.observe(this) {
            setContent() {
                AppTheme {
                    LazyColumn {
                        merlinViewModel.list.value?.size?.let {
                            items(it) { course ->
                                ListItem(headlineContent = {
                                    Text(text = merlinViewModel.list.observeAsState().value?.get(course)?.fullname!!)
                                }, modifier = Modifier.clickable(onClick = {
                                    merlinViewModel.getCourseData(merlinViewModel.list.value?.get(course)?.id!!)
                                }))
                            }
                        }
                        merlinViewModel.list2.value?.size?.let {
                            items(it) { courseDet ->
                                ListItem(headlineContent = {
                                    Text(text = merlinViewModel.list2.observeAsState().value?.get(courseDet)?.name!!)
                                },
                                    leadingContent = {
                                        when (merlinViewModel.list2.value?.get(courseDet)?.module!!) {
                                            "feedback" -> Icon(
                                                painter = painterResource(id = R.drawable.ikonaanketa),
                                                contentDescription = "ikonaAnketa",
                                                modifier = Modifier.background(color = Color(0xff12a676))
                                            )

                                            "folder" -> Icon(
                                                painter = painterResource(id = R.drawable.ikonamapa),
                                                contentDescription = "ikonaFolder",
                                                modifier = Modifier.background(color = Color(0xFF1e83cc))
                                            )

                                            "resource" -> Icon(
                                                painter = painterResource(id = R.drawable.ikonadatoteka),
                                                contentDescription = "ikonaDatoteka",
                                                modifier = Modifier.background(color = Color(0xFF1e83cc))
                                            )

                                            "forum" -> Icon(
                                                painter = painterResource(id = R.drawable.ikonaforum),
                                                contentDescription = "ikonaDokument",
                                                modifier = Modifier.background(color = Color(0xFFc32109))
                                            )

                                            "url" -> Icon(
                                                painter = painterResource(id = R.drawable.ikonapoveznica),
                                                contentDescription = "ikonaLink",
                                                modifier = Modifier.background(color = Color(0xFF1e83cc))
                                            )
                                            "choicegroup" -> Icon(
                                                painter = painterResource(id = R.drawable.ikonaodabirgrupe),
                                                contentDescription = "ikonaodabirgrupe",
                                                modifier = Modifier.background(color = Color(0xFFc32109))

                                            )
                                            "page" -> Icon(
                                                painter = painterResource(id = R.drawable.ikonastranica),
                                                contentDescription = "ikonaPage",
                                                modifier = Modifier.background(color = Color(0xFF1e83cc))
                                            )
                                            "quiz" -> Icon(
                                                painter = painterResource(id = R.drawable.ikonatest),
                                                contentDescription = "ikonaQuiz",
                                                modifier = Modifier.background(color = Color(0xFFab165a))
                                            )
                                            "assign" -> Icon(
                                                painter = painterResource(id = R.drawable.ikonazadaca),
                                                contentDescription = "ikonaUpload",
                                                modifier = Modifier.background(color = Color(0xFFab165a))
                                            )
                                            else -> Icon(
                                                painter = painterResource(id = R.drawable.ikonadatoteka),
                                                contentDescription = "ikonaOstalo",
                                                modifier = Modifier.background(color = Color(0xFF1e83cc))
                                            )
                                        }

                                    })
                            }
                        }
                    }
                }
            }
        }
        onBackListen()

    }


    private fun onBackListen() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val a = Intent(Intent.ACTION_MAIN)
                a.addCategory(Intent.CATEGORY_HOME)
                a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(a)
            }
        })
    }


}
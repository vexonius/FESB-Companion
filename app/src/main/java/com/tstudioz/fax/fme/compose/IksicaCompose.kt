package com.tstudioz.fax.fme.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.models.Receipt
import com.tstudioz.fax.fme.database.models.ReceiptItem
import com.tstudioz.fax.fme.feature.iksica.IksicaViewModel
import com.tstudioz.fax.fme.viewmodel.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.time.LocalDate
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@OptIn(InternalCoroutinesApi::class, ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class)
@Composable
fun IksicaCompose(
    iksicaViewModel: IksicaViewModel,
    mainViewModel: MainViewModel
) {

    val scaffoldState = rememberBottomSheetScaffoldState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BottomSheetScaffold(sheetPeekHeight = 0.dp,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        scaffoldState = scaffoldState,
        sheetContent = {
            if (iksicaViewModel.showItem.observeAsState(initial = false).value) {
                val receipt = iksicaViewModel.itemToShow.observeAsState().value
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ModalBottomSheet(
                    sheetState = sheetState,
                    onDismissRequest = { iksicaViewModel.toggleShowItem(false) },
                ) {
                    IksicaReceiptDetailed(receipt)
                }
            }
        }) {
        val list = iksicaViewModel.receipts.observeAsState().value
        if (!list.isNullOrEmpty()) {
            LazyColumn {
                item {
                    ElevatedCardIksica(
                        mainViewModel.studentDataIksica.observeAsState().value?.nameSurname ?: "",
                        mainViewModel.studentDataIksica.observeAsState().value?.iksicaNumber ?: "",
                        mainViewModel.iksicaBalance.observeAsState().value?.balance.toString()
                    )
                }
                items(list) {
                    IksicaItem(it) {
                        iksicaViewModel.getReceiptDetails(it)
                    }
                }
            }
        } else {
            IksicaLoading(mainViewModel.loadingTxt.observeAsState().value ?: "Loading...")
        }
    }
}

@Preview
@Composable
fun ElevatedCardIksica(
    name: String = "Ime Prezime",
    iksicaNumber: String = "0000000000000000000",
    balance: String = "0.00"
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.586f)
            .padding(25.dp)
            .clip(shape = RoundedCornerShape(10.dp))
            .angledGradientBackground(
                colors = listOf(
                    Color(0xFFfa2531),
                    Color(0xFF0075B2)
                ),
                degrees = 45f,
                halfHalf = true
            )
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                Modifier
                    .padding(25.dp, 25.dp, 0.dp, 0.dp)
                    .weight(0.7f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(0.dp, 5.dp)
                ) {
                    Text(
                        text = name,
                        fontSize = 25.sp,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .padding(0.dp, 5.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = iksicaNumber,
                        fontSize = 20.sp,
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(0.dp, 0.dp, 25.dp, 25.dp)
                    .weight(0.3f)
                    .fillMaxSize()
            ) {
                Text(
                    text = "$balance €",
                    fontSize = 25.sp,
                    lineHeight = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun IksicaItem(receipt: Receipt, onClick: () -> Unit) {
    Column {
        HorizontalDivider()
        ListItem(modifier = Modifier.clickable(onClick = onClick),
            headlineContent = { Text(receipt.restoran, overflow = TextOverflow.Ellipsis) },
            supportingContent = {
                Text(receipt.datumString + " " + receipt.vrijeme + " ")
            },
            overlineContent = { Text(receipt.autorizacija) },
            trailingContent = {
                Text(
                    text = receipt.iznosRacuna.toString() + " €",
                    fontSize = 15.sp,
                )
            })
    }
}

@Preview
@Composable
fun IksicaItemPreview() {
    IksicaItem(
        receipt = Receipt(
            restoran = "Restoran",
            datumString = "Datum",
            vrijeme = "Vrijeme",
            detaljiRacuna = listOf(
                ReceiptItem(
                    nazivArtikla = "Naziv",
                    kolicina = 1,
                    cijenaUkupno = 0.55,
                    iznosSubvencije = 0.27,
                    cijenaJednogArtikla = 0.58
                )
            ),
            iznosRacuna = 0.55,
            iznosSubvencije = 0.27,
            autorizacija = "Autorizacija",
            urlSastavnica = "https://www.google.com",
            datum = LocalDate.now()
        )
    ) {}
}

@Composable
fun IksicaLoading(loadingTxt: String = "Loading...") {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = loadingTxt)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator()
        }
    }
}

@Preview
@Composable
fun IksicaReceiptDetailed(
    receipt: Receipt? = Receipt(
        restoran = "Restoran",
        datumString = "Datum",
        vrijeme = "Vrijeme",
        detaljiRacuna = listOf(
            ReceiptItem(
                nazivArtikla = "Naziv",
                kolicina = 1,
                cijenaUkupno = 0.55,
                iznosSubvencije = 0.27,
                cijenaJednogArtikla = 0.58
            )
        ),
        iznosRacuna = 0.55,
        iznosSubvencije = 0.27,
        autorizacija = "Autorizacija",
        urlSastavnica = "https://www.google.com",
        datum = LocalDate.now()
    )
) {
    LazyColumn(modifier = Modifier.padding(10.dp)) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(10.dp, 5.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.location_pin_svgrepo_com),
                    contentDescription = "Lokacija",
                    Modifier.height(20.dp)
                )
                Text(text = receipt?.restoran ?: "")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(10.dp, 5.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.date_time_svgrepo_com),
                    contentDescription = "Vrijeme",
                    Modifier.height(20.dp)
                )
                Text(text = (receipt?.datumString ?: "") + ", " + (receipt?.vrijeme ?: ""))
            }
        }
        items(receipt?.detaljiRacuna ?: emptyList()) {
            IksicaItemDetailed(it)
        }
        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(text = "Ukupno plaćeno: ")
                Text(text = receipt?.iznosRacuna.toString() + " €")
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(text = "Ukupno subvencionirano: ")
                Text(text = receipt?.iznosSubvencije.toString() + " €")
            }
        }
    }
}

@Preview
@Composable
fun IksicaItemDetailed(
    item: ReceiptItem = ReceiptItem(
        nazivArtikla = "Naziv",
        kolicina = 1,
        cijenaUkupno = 0.55,
        iznosSubvencije = 0.27,
        cijenaJednogArtikla = 0.58
    )
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(Modifier.weight(0.85f)) {
            Text(text = item.kolicina.toString() + "x")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = item.nazivArtikla)
        }
        Column(Modifier.padding(start = 15.dp)) {
            Text(text = item.cijenaUkupno.toString() + " €")
            Text(text = item.iznosSubvencije.toString() + " €", color = Color.Red)
        }
        Column(Modifier.padding(start = 15.dp)) {
            Text(text = item.cijenaUkupno.times(item.kolicina).toString() + " €")
            Text(text = item.iznosSubvencije.times(item.kolicina).toString() + " €", color = Color.Red)
        }
    }
    HorizontalDivider()
}

@Preview
@Composable
fun Test() {
    IksicaItemDetailed(
        item = ReceiptItem(
            nazivArtikla = "Naziva a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a",
            kolicina = 1,
            cijenaUkupno = 0.55,
            iznosSubvencije = 0.27,
            cijenaJednogArtikla = 0.58
        )
    )
}

fun Modifier.angledGradientBackground(colors: List<Color>, degrees: Float, halfHalf: Boolean = false) =
    drawBehind {
        /*
        Have to compute length of gradient vector so that it lies within
        the visible rectangle.
        --------------------------------------------
        | length of gradient ^  /                  |
        |             --->  /  /                   |
        |                  /  / <- rotation angle  |
        |                 /  o --------------------|  y
        |                /  /                      |
        |               /  /                       |
        |              v  /                        |
        --------------------------------------------
                             x

                   diagonal angle = atan2(y, x)
                 (it's hard to draw the diagonal)

        Simply rotating the diagonal around the centre of the rectangle
        will lead to points outside the rectangle area. Further, just
        truncating the coordinate to be at the nearest edge of the
        rectangle to the rotated point will distort the angle.
        Let α be the desired gradient angle (in radians) and γ be the
        angle of the diagonal of the rectangle.
        The correct for the length of the gradient is given by:
        x/|cos(α)|  if -γ <= α <= γ,   or   π - γ <= α <= π + γ
        y/|sin(α)|  if  γ <= α <= π - γ, or π + γ <= α <= 2π - γ
        where γ ∈ (0, π/2) is the angle that the diagonal makes with
        the base of the rectangle.

        */

        var deg2 = degrees

        val (x, y) = size
        val gamma = atan2(y, x)

        if (halfHalf) {
            deg2 = atan2(x, y).times(180f / PI).toFloat()
        }

        if (gamma == 0f || gamma == (PI / 2).toFloat()) {
            // degenerate rectangle
            return@drawBehind
        }

        val degreesNormalised = (deg2 % 360).let { if (it < 0) it + 360 else it }

        val alpha = (degreesNormalised * PI / 180).toFloat()

        val gradientLength = when (alpha) {
            // ray from centre cuts the right edge of the rectangle
            in 0f..gamma, in (2 * PI - gamma)..2 * PI -> {
                x / cos(alpha)
            }
            // ray from centre cuts the top edge of the rectangle
            in gamma..(PI - gamma).toFloat() -> {
                y / sin(alpha)
            }
            // ray from centre cuts the left edge of the rectangle
            in (PI - gamma)..(PI + gamma) -> {
                x / -cos(alpha)
            }
            // ray from centre cuts the bottom edge of the rectangle
            in (PI + gamma)..(2 * PI - gamma) -> {
                y / -sin(alpha)
            }
            // default case (which shouldn't really happen)
            else -> hypot(x, y)
        }

        val centerOffsetX = cos(alpha) * gradientLength / 2
        val centerOffsetY = sin(alpha) * gradientLength / 2

        drawRect(
            brush = Brush.linearGradient(
                colors = colors,
                // negative here so that 0 degrees is left -> right and 90 degrees is top -> bottom
                start = Offset(center.x - centerOffsetX, center.y - centerOffsetY),
                end = Offset(center.x + centerOffsetX, center.y + centerOffsetY)
            ),
            size = size
        )
    }
package com.example.ptoyecto

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Eventos : AppCompatActivity() {

    private val eventsList = mutableListOf<Event>()
    private lateinit var eventsAdapter: EventsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos)

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val db = FirebaseFirestore.getInstance()

        setupTopBar()
        setupTabLayout()
        setupDayCards()
        setupFeaturedEvent()
        setupEventsList()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        user?.let { currentUser ->
            val uid = currentUser.uid
            val docRef = db.collection("usuarios").document(uid)

            docRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                }
            }
        }
    }

    private fun setupTopBar() {
        val backButton: Button = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
            finish()
        }

        val calendarButton: ImageButton = findViewById(R.id.calendar_button)
        calendarButton.setOnClickListener {
        }
    }

    private fun setupTabLayout() {
        val tabLayout: TabLayout = findViewById(R.id.event_tabs)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                filterEventsByCategory(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }

    private fun setupDayCards() {
        val dayCards = listOf(
            findViewById<CardView>(R.id.day_card_1),
            findViewById<CardView>(R.id.day_card_2),
            findViewById<CardView>(R.id.day_card_3),
            findViewById<CardView>(R.id.day_card_4),
            findViewById<CardView>(R.id.day_card_5),
            findViewById<CardView>(R.id.day_card_6)
        )

        dayCards.forEachIndexed { index, cardView ->
            cardView.setOnClickListener {
                selectDayCard(index, dayCards)
                loadEventsForDay(index)
            }
        }
    }

    private fun selectDayCard(selectedIndex: Int, dayCards: List<CardView>) {
        dayCards.forEachIndexed { index, cardView ->
            if (index == selectedIndex) {
                cardView.setCardBackgroundColor(resources.getColor(R.color.fondo_oscuro, theme))
                val cardContent = (cardView.getChildAt(0) as ViewGroup)
                for (i in 0 until cardContent.childCount) {
                    if (cardContent.getChildAt(i) is TextView) {
                        (cardContent.getChildAt(i) as TextView).setTextColor(resources.getColor(android.R.color.white, theme))
                    }
                }
            } else {
                cardView.setCardBackgroundColor(resources.getColor(android.R.color.white, theme))
                val cardContent = (cardView.getChildAt(0) as ViewGroup)
                for (i in 0 until cardContent.childCount) {
                    if (cardContent.getChildAt(i) is TextView) {
                        if (i == 1) {
                            (cardContent.getChildAt(i) as TextView).setTextColor(resources.getColor(R.color.fondo_oscuro, theme))
                        } else {
                            (cardContent.getChildAt(i) as TextView).setTextColor(resources.getColor(android.R.color.darker_gray, theme))
                        }
                    }
                }
            }
        }
    }

    private fun setupFeaturedEvent() {
        val featuredEventCard: CardView = findViewById(R.id.featured_event_card)
        featuredEventCard.setOnClickListener {
            val intent = Intent(this, EventoDetalle::class.java).apply {
                putExtra("EVENT_ID", "featured_event_id")
                putExtra("EVENT_TITLE", "Jornada de Innovación Científica")
            }
            startActivity(intent)
        }
    }

    private fun setupEventsList() {
        val recyclerView: RecyclerView = findViewById(R.id.events_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        createSampleEvents()

        eventsAdapter = EventsAdapter(eventsList) { event ->
            val intent = Intent(this, EventoDetalle::class.java).apply {
                putExtra("EVENT_ID", event.id)
                putExtra("EVENT_TITLE", event.title)
            }
            startActivity(intent)
        }

        recyclerView.adapter = eventsAdapter
    }

    private fun createSampleEvents() {
        eventsList.add(Event(
            "1",
            "Taller de Programación Android",
            "Facultad de Ingeniería, Aula 103",
            "29/04/2025",
            "14:00 - 16:00",
            "Académico"
        ))

        eventsList.add(Event(
            "2",
            "Conferencia: Inteligencia Artificial Ética",
            "Auditorio Principal",
            "29/04/2025",
            "15:30 - 17:30",
            "Conferencia"
        ))

        eventsList.add(Event(
            "3",
            "Exposición de Arte Universitario",
            "Galería Central",
            "29/04/2025",
            "10:00 - 20:00",
            "Cultural"
        ))

        eventsList.add(Event(
            "4",
            "Torneo de Fútbol Interfacultades",
            "Campo Deportivo Norte",
            "29/04/2025",
            "16:00 - 19:00",
            "Deportivo"
        ))

        eventsList.add(Event(
            "5",
            "Charla: Oportunidades de Intercambio",
            "Edificio Internacional, Sala 2",
            "29/04/2025",
            "11:00 - 12:30",
            "Académico"
        ))
    }

    private fun filterEventsByCategory(categoryPosition: Int) {
        val categories = listOf("Todos", "Académicos", "Culturales", "Deportivos", "Conferencias")
        val selectedCategory = categories[categoryPosition]

        val filteredEvents = if (selectedCategory == "Todos") {
            createSampleEvents()
            eventsList
        } else {
            val categoryFilter = when (selectedCategory) {
                "Académicos" -> "Académico"
                "Culturales" -> "Cultural"
                "Deportivos" -> "Deportivo"
                "Conferencias" -> "Conferencia"
                else -> ""
            }

            eventsList.filter { it.category == categoryFilter }
        }

        eventsAdapter.updateEvents(filteredEvents)
    }

    private fun loadEventsForDay(dayIndex: Int) {

        val dates = listOf(
            "29/04/2025", // Monday
            "30/04/2025", // Tuesday
            "01/05/2025", // Wednesday
            "02/05/2025", // Thursday
            "03/05/2025", // Friday
            "04/05/2025"  // Saturday
        )

        // Sample events for each day
        val newEvents = when (dayIndex) {
            0 -> { // Monday
                createSampleEvents()
                eventsList
            }
            1 -> { // Tuesday
                listOf(
                    Event("6", "Presentación de Proyectos Finales", "Facultad de Ciencias, Aula Magna",
                        "30/04/2025", "09:00 - 12:00", "Académico"),
                    Event("7", "Concierto de Música Clásica", "Teatro Universitario",
                        "30/04/2025", "18:00 - 20:00", "Cultural"),
                    Event("8", "Seminario de Investigación", "Biblioteca Central, Sala 3",
                        "30/04/2025", "14:00 - 16:00", "Académico")
                )
            }
            2 -> { // Wednesday
                listOf(
                    Event("9", "Día del Trabajo - No hay actividades", "Universidad cerrada",
                        "01/05/2025", "Todo el día", "Informativo")
                )
            }
            3 -> { // Thursday
                listOf(
                    Event("10", "Feria de Empleo", "Pabellón Central",
                        "02/05/2025", "10:00 - 18:00", "Académico"),
                    Event("11", "Debate: Cambio Climático", "Facultad de Ciencias Ambientales",
                        "02/05/2025", "16:00 - 18:00", "Conferencia"),
                    Event("12", "Torneo de Voleibol", "Polideportivo",
                        "02/05/2025", "15:00 - 19:00", "Deportivo")
                )
            }
            4 -> { // Friday
                listOf(
                    Event("13", "Festival Gastronómico", "Plaza Central",
                        "03/05/2025", "12:00 - 20:00", "Cultural"),
                    Event("14", "Presentación de Libro", "Biblioteca Central",
                        "03/05/2025", "17:00 - 19:00", "Cultural"),
                    Event("15", "Competencia de Robótica", "Laboratorio de Ingeniería",
                        "03/05/2025", "09:00 - 14:00", "Académico")
                )
            }
            5 -> { // Saturday
                listOf(
                    Event("16", "Taller de Fotografía", "Facultad de Artes",
                        "04/05/2025", "10:00 - 13:00", "Cultural"),
                    Event("17", "Maratón Universitario", "Campus Completo",
                        "04/05/2025", "08:00 - 11:00", "Deportivo")
                )
            }
            else -> emptyList()
        }

        updateFeaturedEvent(dayIndex)

        eventsAdapter.updateEvents(newEvents)

        val dayNames = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
        val eventsLabel: TextView = findViewById(R.id.events_label)
        eventsLabel.text = "Eventos del ${dayNames[dayIndex]}"
    }

    private fun updateFeaturedEvent(dayIndex: Int) {
        val featuredTitles = listOf(
            "Jornada de Innovación Científica",
            "Foro de Empleabilidad Internacional",
            "Sin eventos destacados",
            "Jornada de Salud Mental",
            "Festival de Arte y Música",
            "Hackathon Universitario"
        )

        val featuredLocations = listOf(
            "Auditorio Principal",
            "Centro de Conferencias",
            "",
            "Facultad de Psicología",
            "Plaza Central",
            "Centro de Innovación"
        )

        val featuredTimes = listOf(
            "10:00 - 13:00",
            "11:00 - 14:00",
            "",
            "09:00 - 15:00",
            "16:00 - 22:00",
            "08:00 - 20:00"
        )

        val featuredDates = listOf(
            "29 Abril, 2025",
            "30 Abril, 2025",
            "01 Mayo, 2025",
            "02 Mayo, 2025",
            "03 Mayo, 2025",
            "04 Mayo, 2025"
        )

        findViewById<TextView>(R.id.featured_event_title).text = featuredTitles[dayIndex]
        findViewById<TextView>(R.id.featured_event_location).text = featuredLocations[dayIndex]
        findViewById<TextView>(R.id.featured_event_time).text = featuredTimes[dayIndex]
        findViewById<TextView>(R.id.featured_event_date).text = featuredDates[dayIndex]

        if (dayIndex == 2) {
            findViewById<CardView>(R.id.featured_event_card).visibility = View.GONE
            findViewById<TextView>(R.id.featured_label).visibility = View.GONE
        } else {
            findViewById<CardView>(R.id.featured_event_card).visibility = View.VISIBLE
            findViewById<TextView>(R.id.featured_label).visibility = View.VISIBLE
        }
    }
}


data class Event(
    val id: String,
    val title: String,
    val location: String,
    val date: String,
    val time: String,
    val category: String
)

class EventsAdapter(
    private var events: List<Event>,
    private val onEventClick: (Event) -> Unit
) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    fun updateEvents(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
        holder.itemView.setOnClickListener { onEventClick(event) }
    }

    override fun getItemCount() = events.size

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventTitle: TextView = itemView.findViewById(R.id.event_title)
        private val eventLocation: TextView = itemView.findViewById(R.id.event_location)
        private val eventTime: TextView = itemView.findViewById(R.id.event_time)
        private val eventCategory: TextView = itemView.findViewById(R.id.event_category)

        fun bind(event: Event) {
            eventTitle.text = event.title
            eventLocation.text = event.location
            eventTime.text = event.time
            eventCategory.text = event.category

            val categoryBackground = when (event.category) {
                "Académico" -> R.drawable.academic_category_background
                "Cultural" -> R.drawable.cultural_category_background
                "Deportivo" -> R.drawable.sports_category_background
                "Conferencia" -> R.drawable.conference_category_background
                else -> R.drawable.default_category_background
            }
            eventCategory.setBackgroundResource(categoryBackground)
        }
    }
}
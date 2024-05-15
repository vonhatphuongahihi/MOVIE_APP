package com.example.movieapp.Activities.ui.dashboard

import Movie
import MovieAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() , AdapterView.OnItemClickListener{

    private var _binding: FragmentDashboardBinding? = null

    private var movieList:ArrayList<Movie>?=null
    private var movieAdapter:MovieAdapter?=null
    private var gridView:GridView?=null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
     gridView =root.findViewById(R.id.gridView)
        movieList=ArrayList()
        movieList= setDataList()
        movieAdapter=MovieAdapter(requireContext(), movieList!!)
        gridView?.adapter=movieAdapter
        return root
    }
    private fun setDataList(): ArrayList<Movie>{
        var arrayList:ArrayList<Movie> = ArrayList()

        arrayList.add(Movie(R.drawable.death_note_home, "fdsafsadf",3242))
        arrayList.add(Movie(R.drawable.death_note_home, "fdsafsadf",3242))
        arrayList.add(Movie(R.drawable.death_note_home, "fdsafsadf",3242))
        arrayList.add(Movie(R.drawable.death_note_home, "fdsafsadf",3242))
        arrayList.add(Movie(R.drawable.death_note_home, "fdsafsadf",3242))
        return arrayList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }
}
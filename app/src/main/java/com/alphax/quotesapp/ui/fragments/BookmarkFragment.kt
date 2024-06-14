package com.alphax.quotesapp.ui.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alphax.quotesapp.R
import com.alphax.quotesapp.ui.QuotesActivity
import com.alphax.quotesapp.ui.adapters.SavedQuotesAdapter
import com.alphax.quotesapp.viewmodels.QuoteViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_bookmarks.*

@AndroidEntryPoint
class BookmarkFragment : Fragment(R.layout.fragment_bookmarks) {

    // variables
    private val viewModel by activityViewModels<QuoteViewModel>() // getting viewModel linked to activity
    lateinit var savedQuotesAdapter: SavedQuotesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        // copy quote when the item is long clicked
        savedQuotesAdapter.setOnItemLongClickListener {
            val clipBoardManager = (activity as QuotesActivity)
                .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            val clipdata = ClipData.newPlainText(
                "quote",
                "\"${it.quote}\"\n\n- ${it.author}"
            )

            clipBoardManager.setPrimaryClip(clipdata)

            if (!(activity as QuotesActivity).atHome) Snackbar.make(
                view,
                "Quote Copied!",
                Snackbar.LENGTH_SHORT
            ).show()
            true
        }

        // callback which defines what should be done when items are swiped
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            // delete the quote on Swipe
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val quote = savedQuotesAdapter.differ.currentList[position]
                viewModel.deleteQuote(quote)
                Snackbar.make(view, "Removed Bookmark!", Snackbar.LENGTH_SHORT)
                    .apply {
                        // if the click was in error, then provide re-saving option
                        setAction("Undo") {
                            viewModel.saveQuote(quote)
                            if ((activity as QuotesActivity?) != null && !(activity as QuotesActivity).atHome) Snackbar.make(
                                view,
                                "Re-saved!",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        setActionTextColor(ContextCompat.getColor(view.context, R.color.light_blue))
                        show()
                    }
            }
        }

        // attach the swipe behavior to each recycler view item
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedQuotes)
        }

        // observe data changes and apply them to the recycler view
        viewModel.getSavedQuotes().observe(viewLifecycleOwner) { articles ->
            savedQuotesAdapter.differ.submitList(articles)

            // if no quotes present, then show textview and hide recyclerview
            if (articles.isEmpty()) {
                rvSavedQuotes.visibility = View.GONE
                tvNoBookmarks.visibility = View.VISIBLE
            } else {
                rvSavedQuotes.visibility = View.VISIBLE
                tvNoBookmarks.visibility = View.GONE
            }
        }
    }

    // function to set adapter and layout manager on the recycler view
    private fun setupRecyclerView() {
        savedQuotesAdapter = SavedQuotesAdapter()
        rvSavedQuotes.apply {
            adapter = savedQuotesAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}

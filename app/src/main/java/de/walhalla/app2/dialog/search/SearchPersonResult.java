package de.walhalla.app2.dialog.search;

import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.walhalla.app2.App;
import de.walhalla.app2.dialog.DrinkDialog;
import de.walhalla.app2.dialog.DrinkInvoiceDialog;
import de.walhalla.app2.fragment.drink.Fragment;
import de.walhalla.app2.interfaces.Kinds;
import de.walhalla.app2.model.Person;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;

/**
 * @author B3tterTogeth3r
 * @version 1.0
 * @see ir.mirrajabi.searchdialog.core.SearchResultListener
 * @since 2.5
 */
public class SearchPersonResult extends Fragment implements SearchResultListener<Searchable> {
    private final String kind;

    public SearchPersonResult(@Kinds.Search String kind) {
        this.kind = kind;
    }

    @Override
    public void onSelected(BaseSearchDialogCompat dialog, @NotNull Searchable item, int position) {
        Person person = findByFullName(item.getTitle());
        if (person == null) {
            Toast.makeText(App.getContext(), "This person is not selectable",
                    Toast.LENGTH_LONG).show();
        } else {
            switch (kind) {
                case Kinds.DRINK_INVOICE:
                    // Open invoice dialog
                    new DrinkInvoiceDialog(Fragment.context, person).show();
                    break;
                case Kinds.DRINK_PAYMENT:
                case Kinds.DRINK_FINE:
                    // Open dialog
                    new DrinkDialog(Fragment.context, person, kind).show();
                    break;
                default:
                    Toast.makeText(App.getContext(), "This person is not selectable",
                            Toast.LENGTH_LONG).show();
            }
        }
        dialog.dismiss();
    }

    @Nullable
    private Person findByFullName(String title) {
        for (Person p : Fragment.persons) {
            if (p.getFullName().equals(title))
                return p;
        }
        return null;
    }
}

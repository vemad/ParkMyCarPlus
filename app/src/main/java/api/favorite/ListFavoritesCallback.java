package api.favorite;

/**
 * Created by Gaetan on 09/03/2015.
 */
public abstract class ListFavoritesCallback {
    protected abstract void callback(Exception e, Favorite[] listFavorites);
}

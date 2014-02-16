/*
 * Copyright (C) 2008 Romain Guy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.curiouscreature.android.shelves.provider;

import org.curiouscreature.android.shelves.util.ImageUtilities;
import org.curiouscreature.android.shelves.util.ImportUtilities;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;

public class BooksManager {
    static final int BOOK_COVER_WIDTH = 100;
    static final int BOOK_COVER_HEIGHT = 120;

    private static String sBookIdSelection;
    private static String sBookSelection;

    private static String[] sArguments1 = new String[1];
    private static String[] sArguments3 = new String[3];

    private static final String[] PROJECTION_ID_IID = new String[] {
            BooksStore.Book._ID, BooksStore.Book.INTERNAL_ID
    };
    private static final String[] PROJECTION_ID = new String[] { BooksStore.Book._ID };

    static {//仅在类被加载的时候执行且仅被执行一次
        StringBuilder selection = new StringBuilder();
        selection.append(BooksStore.Book.INTERNAL_ID);
        selection.append("=?");
        sBookIdSelection = selection.toString();
        
        /*
         * stringbuilder 主要是解决对字符串做频繁修改操作时的性能问题，
         * 有一个容量的概念，先分配好一定的内存，在字符串长度达到上限之前，全部在此内存内操作，不涉及内存的重新分配和回收。
		 * 而 string 每次修改都会重新创建一个对象。
         * */
        selection = new StringBuilder();
        selection.append(sBookIdSelection);
        selection.append(" OR ");
        selection.append(BooksStore.Book.EAN);
        selection.append("=? OR ");
        selection.append(BooksStore.Book.ISBN);
        selection.append("=?");
        /*
         * toString()方法就是把对象转换成String类型，转换的算法根据类型和实际需要而定
         * 所谓的把对象转换，其实是指对象中的成员变量而言，可以实例化的对象和不可实例化的对象
         * 的区别就是前者有了具体的内存分配
         * */
        sBookSelection = selection.toString();
    }

    private BooksManager() {
    }
    
    public static String findBookId(ContentResolver contentResolver, String id) {
        String internalId = null;
        Cursor c = null;

        try {
            final String[] arguments3 = sArguments3;
            arguments3[0] = arguments3[1] = arguments3[2] = id;
            c = contentResolver.query(BooksStore.Book.CONTENT_URI, PROJECTION_ID_IID,
                    sBookSelection, arguments3, null);
            if (c.getCount() > 0) {
                if (c.moveToFirst()) {
                    internalId = c.getString(c.getColumnIndexOrThrow(BooksStore.Book.INTERNAL_ID));
                }
            }
        } finally {
            if (c != null) c.close();
        }

        return internalId;
    }

    public static boolean bookExists(ContentResolver contentResolver, String id) {
        boolean exists;
        Cursor c = null;

        try {
            final String[] arguments3 = sArguments3;
            arguments3[0] = arguments3[1] = arguments3[2] = id;
            c = contentResolver.query(BooksStore.Book.CONTENT_URI, PROJECTION_ID, sBookSelection,
                    arguments3, null);
            exists = c.getCount() > 0;
        } finally {
            if (c != null) c.close();
        }

        return exists;
    }

    public static BooksStore.Book loadAndAddBook(ContentResolver resolver, String id,
            BooksStore booksStore) {

        final BooksStore.Book book = booksStore.findBook(id);
        if (book != null) {
            Bitmap bitmap = book.loadCover(BooksStore.ImageSize.TINY);
            if (bitmap != null) {
                bitmap = ImageUtilities.createBookCover(bitmap, BOOK_COVER_WIDTH, BOOK_COVER_HEIGHT);
                ImportUtilities.addBookCoverToCache(book, bitmap);
            }

            final Uri uri = resolver.insert(BooksStore.Book.CONTENT_URI, book.getContentValues());
            if (uri != null) {
                return book;
            }
        }

        return null;
    }

    public static boolean deleteBook(ContentResolver contentResolver, String bookId) {
        final String[] arguments1 = sArguments1;
        arguments1[0] = bookId;
        int count = contentResolver.delete(BooksStore.Book.CONTENT_URI,
                sBookIdSelection, arguments1);
        ImageUtilities.deleteCachedCover(bookId);
        return count > 0;
    }

    public static BooksStore.Book findBook(ContentResolver contentResolver, String id) {
        BooksStore.Book book = null;
        Cursor c = null;

        try {
            sArguments1[0] = id;
            c = contentResolver.query(BooksStore.Book.CONTENT_URI, null, sBookIdSelection,
                    sArguments1, null);
            if (c.getCount() > 0) {
                if (c.moveToFirst()) {
                    book = BooksStore.Book.fromCursor(c);
                }
            }
        } finally {
            if (c != null) c.close();
        }

        return book;
    }

    public static BooksStore.Book findBook(ContentResolver contentResolver, Uri data) {
        BooksStore.Book book = null;
        Cursor c = null;

        try {
            c = contentResolver.query(data, null, null, null, null);
            if (c.getCount() > 0) {
                if (c.moveToFirst()) {
                    book = BooksStore.Book.fromCursor(c);
                }
            }
        } finally {
            if (c != null) c.close();
        }

        return book;
    }
}

const listElm = document.querySelector('#infinite-list');
const contentElm = document.querySelector('.meme-content');

let nextItem = 1;
const loadMore = function() {
    debugger
    window.location.replace('?page=' + 2);
    // for (var i = 0; i < 20; i++) {
    //     var item = document.createElement('li');
    //     item.innerText = 'Item ' + nextItem++;
    //
    //
    //
    //     listElm.appendChild(item);
    // }
};

const appendItems = function(items) {
    items.forEach((item) => {
        const memeItem = document.createElement('meme-item');

        memeItem.title = item.title;
        memeItem.score = item.score;
        memeItem.publishdate = item.publishDate;
        memeItem.memeid = item.id;
        memeItem.fullmediaurl = item.fullMediaUrl;

        listElm.appendChild(memeItem);
    });
};

// Detect when scrolled to bottom.
listElm.addEventListener('scroll', function() {
    if (listElm.scrollTop + listElm.clientHeight >= listElm.scrollHeight) {
        loadMore();
    }
});

contentElm.addEventListener('scroll', function() {
    if (contentElm.scrollTop + contentElm.clientHeight >= contentElm.scrollHeight) {
        loadMore();
    }
});
const listElm = document.querySelector('#infinite-list');

let nextItem = 1;
const loadMore = function() {
    for (var i = 0; i < 20; i++) {
        var item = document.createElement('li');
        item.innerText = 'Item ' + nextItem++;



        listElm.appendChild(item);
    }
};

const appendItems = function(items) {
    items.forEach((item) => {
        const itemEl = document.createElement('meme-item');
        itemEl.title = item.title;
        itemEl.score = item.score;
        itemEl.publishdate = item.publishDate;
        itemEl.memeid = item.id;
        itemEl.fullmediaurl = item.fullMediaUrl;

        listElm.appendChild(itemEl);
    });
};

// Detect when scrolled to bottom.
listElm.addEventListener('scroll', function() {
    if (listElm.scrollTop + listElm.clientHeight >= listElm.scrollHeight) {
        loadMore();
    }
});
let observer = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.src = entry.target.dataset.src;
                observer.unobserve(entry.target);
            }
        });
    },
    // {rootMargin: "0px 0px -500px 0px"}
);

const loadMore = async function() {

    const pagePrefix = '/page',
        firstPageNum = 1,
        pathname = window.location.pathname,
        uri = window.location.protocol + '//' + window.location.host;
    let pageNum = pathname && pathname !== '/' && pathname.replace(pagePrefix, '') || firstPageNum,
        url = uri + pagePrefix + (++pageNum);

    let response = await fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    });

    if (response.ok) {
        let json = await response.json();
        let respPageNum = json.number + 1;
        window.history.pushState({page: respPageNum}, null, pagePrefix + respPageNum);

        return json;
    } else {
        throw new Error('Ошибка HTTP: ' + response.status);
    }
};

const appendItems = function(items) {
    const listElm = document.querySelector('#infinite-list');
    items.forEach((item) => {
        const memeItem = document.createElement('meme-item');

        memeItem.title = item.title;
        memeItem.score = item.score;
        memeItem.publishdate = item.publishDate;
        memeItem.memeid = item.id;
        memeItem.fullmediaurl = item.fullMediaUrl;

        memeItem.shadowRoot.querySelectorAll('img').forEach(img => {
            observer.observe(img);
        });
        listElm.appendChild(memeItem);
    });
};

document.addEventListener('scroll', function() {
    if (!window.location.pathname.includes('post')) {
        if (document.documentElement.scrollTop + document.documentElement.clientHeight >= document.documentElement.scrollHeight) {
            const loadMorePromise = loadMore();
            loadMorePromise
                .then(result => {
                    if (result) {
                        appendItems(result.content);
                    }
                })
                .catch(err => {
                    console.log(err);
                });
        }
    }
});
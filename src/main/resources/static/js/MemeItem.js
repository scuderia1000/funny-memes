'use strict';

const template = document.createElement('template');
template.innerHTML = `
    <link rel="stylesheet" href="../../static/css/meme-item.css"/>
    <li class="meme-post-item">
        <span class="meme-title"></span>
        <div class="score-container">
            <span class="score-text"></span>
            <img class="score-like-icon" src="../../static/images/svg/like.svg" alt="like"/>
        </div>
        <span class="score-date"></span>
        <div class="post-message">
            <a href="#" id="meme-content-link">
                <div class="meme-media-content">
                    <img class="media-image">
                </div>
            </a>
        </div>
    </li>
`;

class MemeItem extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
        // const template = document.getElementById('meme-item-template');
        this.shadowRoot.appendChild(template.content.cloneNode(true));
    }

    connectedCallback() {
        debugger
        if (!this.rendered) {
            this.render();
            this.rendered = true;
        }
    }

    disconnectedCallback() {
        // this.querySelector('button').removeEventListener('click', this.close);
        // this.querySelector('.overlay').removeEventListener('click', this.close);
    }

    static get observedAttributes() {
        return ['title', 'score', 'publishdate', 'memeid', 'fullmediaurl'];
    }

    attributeChangedCallback(name, oldValue, newValue) {
        this.render();
    }

    get title() {
        return this.hasAttribute('title');
    }

    set title(val) {
        if (val) {
            this.setAttribute('title', val);
        } else {
            this.removeAttribute('title');
        }
    }

    get score() {
        return this.hasAttribute('score');
    }

    set score(val) {
        if (val) {
            this.setAttribute('score', val);
        } else {
            this.removeAttribute('score');
        }
    }

    get publishdate() {
        return this.hasAttribute('publishdate');
    }

    set publishdate(val) {
        if (val) {
            this.setAttribute('publishdate', val);
        } else {
            this.removeAttribute('publishdate');
        }
    }

    get memeid() {
        return this.hasAttribute('memeid');
    }

    set memeid(val) {
        if (val) {
            this.setAttribute('memeid', val);
        } else {
            this.removeAttribute('memeid');
        }
    }

    get fullmediaurl() {
        return this.hasAttribute('fullmediaurl');
    }

    set fullmediaurl(val) {
        if (val) {
            this.setAttribute('fullmediaurl', val);
        } else {
            this.removeAttribute('fullmediaurl');
        }
    }

    render() {
        const shadowRoot = this.shadowRoot,
            memeId = this.getAttribute('memeid'),
            dateAttr = this.getAttribute('publishdate'),
            fullMediaUrl = this.getAttribute('fullmediaurl');

        shadowRoot.querySelector('.meme-title').innerHTML = this.getAttribute('title') || '';
        shadowRoot.querySelector('.score-text').innerHTML = this.getAttribute('score') || '';

        const publishDate = dateAttr && new Date(dateAttr) || '';
        shadowRoot.querySelector('.score-date').innerHTML = publishDate.toLocaleString();

        if (memeId) {
            shadowRoot.querySelector('#meme-content-link').href = '/post/' + memeId;
        }
        if (fullMediaUrl) {
            shadowRoot.querySelector('.media-image').src = fullMediaUrl;
        }
        this.rendered = true;
    }
}

customElements.define('meme-item', MemeItem);
'use strict';

const template = document.createElement('template');
template.innerHTML = `
    <link rel="stylesheet" href="/css/meme-item.css"/>
    <li class="meme-post-item">
        <span class="meme-title"></span>
        <div class="score-container">
            <span class="score-text"></span>
            <img class="score-like-icon" src="/images/svg/like.svg" alt="like"/>
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
        this.shadowRoot.appendChild(template.content.cloneNode(true));
    }

    connectedCallback() {
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
        return this.getAttribute('title');
    }

    set title(val) {
        this.setAttribute('title', val);
    }

    get score() {
        return this.getAttribute('score');
    }

    set score(val) {
        this.setAttribute('score', val);
    }

    get publishdate() {
        return this.getAttribute('publishdate');
    }

    set publishdate(val) {
        this.setAttribute('publishdate', val);
    }

    get memeid() {
        return this.getAttribute('memeid');
    }

    set memeid(val) {
        this.setAttribute('memeid', val);
    }

    get fullmediaurl() {
        return this.getAttribute('fullmediaurl');
    }

    set fullmediaurl(val) {
        this.setAttribute('fullmediaurl', val);
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
    }
}

customElements.define('meme-item', MemeItem);
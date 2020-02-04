'use strict';

const template = document.createElement('template');
template.innerHTML = `
    <style>
        .meme-post-item {
            display: flex;
            flex-direction: column;
            /*align-items: center;*/
            /*justify-content: center;*/
            padding: 5px 5px 10px 20px;
            background-color: white;
            margin-bottom: 20px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        .meme-title {
            font-weight: 500;
            font-size: 18px;
        }
        .score-container {
            display: flex;
            /*justify-content: center;*/
            align-items: center;
            padding: 5px;
        }
        .score-text,
        .score-date {
            font-size: 14px;
            font-weight: 500;
            margin-right: 5px;
        }
        .score-like-icon {
            width: 18px;
        }
        .post-message {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            max-height: 512px;
        }
        .meme-media-content {
            max-height: 478px;
        }

        .media-image {
            max-height: 400px;
        }
    </style>
    <li class="meme-post-item">
        <span class="meme-title"></span>
        <div class="score-container">
            <span class="score-text"></span>
            <img class="score-like-icon" src="../static/images/svg/like.svg" alt="like"/>
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

        // If your code is inside of an HTML Import you'll need to change the above line to:
        // let tmpl = document.currentScript.ownerDocument.querySelector('#x-foo-from-template');
        // const node = template.content.cloneNode(true);
        // const node = document.importNode(template.content, true);
        // this.appendChild(node);
        this.render();



        // this.querySelector('button').addEventListener('click', this.close);
        // this.querySelector('.overlay').addEventListener('click', this.close);
        // this.open = this.open;
    }

    disconnectedCallback() {
        // this.querySelector('button').removeEventListener('click', this.close);
        // this.querySelector('.overlay').removeEventListener('click', this.close);
    }

    static get observedAttributes() {
        return ['title', 'score', 'publishDate', 'memeId', 'fullMediaUrl'];
    }

    attributeChangedCallback(name, oldValue, newValue) {
        this.render();
    }

    render() {
        debugger
        this.querySelector('.meme-title').innerHTML = this.getAttribute('title');
        this.querySelector('.score-text').innerHTML = this.getAttribute('score');

        const publishDate = new Date(this.getAttribute('publishDate'));
        this.querySelector('.score-date').innerHTML = publishDate.toLocaleString();

        this.querySelector('meme-content-link').href = '/post/' + this.getAttribute('memeId');
        this.querySelector('.media-image').src = this.getAttribute('fullMediaUrl');
    }
}

customElements.define('meme-item', MemeItem);
// @ts-check
// `@type` JSDoc annotations allow editor autocompletion and type checking
// (when paired with `@ts-check`).
// See: https://docusaurus.io/docs/api/docusaurus-config

import {themes as prismThemes} from 'prism-react-renderer';
import title from 'title';

// `strip`  = patterns removed before casing (RegExp or string)
// `special` = title()'s acronym/stop-word dictionary
const normalise = (label, {strip = [], special = []} = {}) => {
    const cleaned = strip
        .reduce((s, p) => s.replace(p instanceof RegExp ? p : new RegExp(p), ''), label)
        .replace(/[-_]/g, ' ')
        .trim();
    return title(cleaned, {special});
};

const CATEGORY = {
    strip:   [/^m\d+-/],              // drops m1-, m2-, m10-, … (one regex covers all)
    special: ['IO', 'API', 'JSON', 'JVM', 'DataFrame', 'DataFrames'],
};

const DOCUMENT = {
    special: ['IO', 'API', 'JSON', 'JVM', 'DataFrame', 'DataFrames'],
};

function normaliseSidebarLabels(items, titleById) {
    // Snake_Case comes from folder/file names (e.g. `Spark_Structured_API_with_DataFrames`);
    // turn the underscores into spaces for every sidebar entry's displayed label.
    const transform = (list) => list.map((item) => {
        if (item.type === 'category') {
            return {...item, label: normalise(item.label, CATEGORY), items: transform(item.items)};
        }
        if (item.type === 'doc') {
            // Doc entries are keyed by `id` (which must stay intact); the label is
            // resolved from the doc's title, so override the label, never the id.
            return {...item, label: normalise(item.label ?? titleById[item.id] ?? item.id, DOCUMENT)};
        }
        // Links and any other labelled entries.
        return item.label ? {...item, label: unsnake(item.label)} : item;
    });
    return transform(items);
}

// This runs in Node.js - Don't use client-side code here (browser APIs, JSX...)

/** @type {import('@docusaurus/types').Config} */
const config = {
    title: 'Spark Essentials',
    tagline: 'Apache Spark with Scala - course notes',
    favicon: 'img/favicon.ico',

    // Future flags, see https://docusaurus.io/docs/api/docusaurus-config#future
    future: {
        v4: true, // Improve compatibility with the upcoming Docusaurus v4
    },

    // Production url of the site (GitHub Pages for the DarrenBishop/spark-essentials repo).
    url: 'https://DarrenBishop.github.io',
    // For project pages the baseUrl is '/<projectName>/'.
    baseUrl: '/spark-essentials/',

    // GitHub pages deployment config (used by `docs / docusaurusPublishGhpages`).
    organizationName: 'DarrenBishop', // GitHub org/user name.
    projectName: 'spark-essentials', // GitHub repo name.

    // In docs-only mode the navbar title/logo links to '/', which only exists once a
    // note carries `slug: /` front matter. Until then 'warn' keeps that link from
    // hard-failing the build; switch back to 'throw' for CI once the note set is stable.
    onBrokenLinks: 'warn',

    markdown: {
        format: 'detect', // .md → CommonMark, .mdx → MDX
        mermaid: true,
    },

    themes: [
        '@docusaurus/theme-mermaid',
    ],

    clientModules: [
        './src/clientModules/d3-transition.js'
    ],

    i18n: {
        defaultLocale: 'en',
        locales: ['en'],
    },

    presets: [
        [
            'classic',
            /** @type {import('@docusaurus/preset-classic').Options} */
            ({
                docs: {
                    // Serve the notes at the site root (docs-only site, no separate homepage).
                    routeBasePath: '/',
                    // mdoc writes the processed Markdown here (default `docs / mdocOut`).
                    // Relative to this website/ directory.
                    path: '../site/target/mdoc',
                    sidebarPath: './sidebars.js',
                    async sidebarItemsGenerator({defaultSidebarItemsGenerator, ...args}) {
                        const items = await defaultSidebarItemsGenerator(args);
                        const titleById = Object.fromEntries(args.docs.map((d) => [d.id, d.title]));
                        return normaliseSidebarLabels(items, titleById);
                    },
                },
                // No blog on a course-notes site.
                blog: false,
                theme: {
                    customCss: './src/css/custom.css',
                },
            }),
        ],
    ],

    themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
        ({
            image: 'img/docusaurus-social-card.jpg',
            colorMode: {
                respectPrefersColorScheme: true,
            },
            navbar: {
                title: 'Spark Essentials',
                logo: {
                    alt: 'Spark Essentials Logo',
                    src: 'img/logo.svg',
                },
                items: [
                    {
                        type: 'docSidebar',
                        sidebarId: 'tutorialSidebar',
                        position: 'left',
                        label: 'Notes',
                    },
                    {
                        href: 'https://github.com/DarrenBishop/spark-essentials',
                        label: 'GitHub',
                        position: 'right',
                    },
                ],
            },
            footer: {
                style: 'dark',
                links: [
                    {
                        title: 'More',
                        items: [
                            {
                                label: 'GitHub',
                                href: 'https://github.com/DarrenBishop/spark-essentials',
                            },
                        ],
                    },
                ],
                copyright: `Copyright © ${new Date().getFullYear()} Darren Bishop. Built with Docusaurus.`,
            },
            prism: {
                theme: prismThemes.github,
                darkTheme: prismThemes.dracula,
                // Prism's `scala` grammar extends `java`, so `java` must load first.
                additionalLanguages: ['java', 'scala'],
            },
        }),
};

export default config;

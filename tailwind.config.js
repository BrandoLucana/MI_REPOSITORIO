import tailwindcss from "@tailwindcss/vite";
/** @type {import("tailwindcss¨).config}*/
module.exports = {
    content: ["./templates/*.html"],
    theme:{
        extend: {},
    },
    plugins: [
        tailwindcss(),
    ],
}
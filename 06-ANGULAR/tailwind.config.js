export default {
    content: [
        "./index.html",
        "./01-Javascript/web/index.html",
        "./src/**/*.{js,ts,jsx,tsx,vue}" // quét tất cả file có class
    ],
    theme: {
        extend: {
            colors: {
                primary: "#1E40AF",
                secondary: "#F59E0B"
            },
            spacing: {
                128: "32rem"
            }
        }
    },
    plugins: [],
}

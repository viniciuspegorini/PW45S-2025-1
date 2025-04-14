
import { Link } from "react-router-dom"

export function NotFound() {
    return (
        <article style={{ padding: "100px" }}>
            <h1>Oops!</h1>
            <p>Page Not Found</p>
            <div className="flexGrow">
                <Link to="/">Home</Link>
            </div>
        </article>
    )
}
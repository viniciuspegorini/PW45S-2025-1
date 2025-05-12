import { Outlet } from "react-router-dom";
import { NavBar } from "../../components/NavBar";

export function Layout() {
  return (
    <>
      <NavBar />
      <main>
        <Outlet />
      </main>
    </>
  );
}

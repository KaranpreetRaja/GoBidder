import Link from "next/link";

export default function Footer() {
  return (
    <footer className="flex justify-between w-full bg-white mt-10 min-h-20 p-5">
      <div>
        <p className="text-xl font-bold">
          GoBidder
        </p>
        <p>&copy; 2024</p>
      </div>
      <div>
        <ul>
          <li>
            <Link className="font-bold hover:underline" href="/">
              Terms of Service
            </Link>
          </li>
          <li className="mt-2">
            <Link className="font-bold hover:underline" href="/">
              Shipping Policy
            </Link>
          </li>
          <li className="mt-2">
            <Link className="font-bold hover:underline" href="/">
              Payment Policy
            </Link>
          </li>
        </ul>
      </div>
    </footer>
  );
}

<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;

class UserController extends Controller
{
    /**
     * Get user profile by token.
     */
    public function index(Request $request)
    {
        $token = $request->query('user_token');
        if (!$token) {
            return response()->json(['message' => 'Token required'], 400);
        }

        $user = User::where('user_token', $token)->first();

        if (!$user) {
            return response()->json(['message' => 'User not found'], 404);
        }

        // If avatar exists, return full URL
        if ($user->avatar) {
            $user->avatar_url = url('storage/' . $user->avatar);
        }

        return response()->json($user);
    }

    /**
     * Update user avatar.
     */
    public function updateAvatar(Request $request)
    {
        $request->validate([
            'user_token' => 'required|string',
            'avatar' => 'required|image|mimes:jpeg,png,jpg,gif|max:2048',
        ]);

        $user = User::where('user_token', $request->user_token)->first();

        if (!$user) {
            return response()->json(['message' => 'User not found'], 404);
        }

        if ($request->hasFile('avatar')) {
            // Delete old avatar if exists
            if ($user->avatar) {
                Storage::disk('public')->delete($user->avatar);
            }

            // Store new avatar
            $path = $request->file('avatar')->store('avatars', 'public');
            $user->avatar = $path;
            $user->save();

            return response()->json([
                'message' => 'Avatar updated successfully',
                'avatar' => $path,
                'avatar_url' => url('storage/' . $path)
            ]);
        }

        return response()->json(['message' => 'No file uploaded'], 400);
    }
}

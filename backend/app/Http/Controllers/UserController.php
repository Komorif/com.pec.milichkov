<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;

class UserController extends Controller
{
    public function index(Request $request)
    {
        return response()->json($request->user());
    }

    /**
     * Get all data (profile + questions if available).
     */
    public function getAllData(Request $request)
    {
        $user = $request->user();
        
        // Prepare base data
        $data = [
            'user' => $user,
        ];

        // Check for questions.json if it exists
        $questionsPath = storage_path('app/questions.json');
        if (file_exists($questionsPath)) {
            $data['questions'] = json_decode(file_get_contents($questionsPath), true);
        }

        return response()->json($data);
    }

    /**
     * Update user avatar.
     */
    public function updateAvatar(Request $request)
    {
        $user = $request->user();

        // Handle preset index selection
        if ($request->has('preset_index')) {
            // Delete old avatar file if it exists
            if ($user->avatar && !str_starts_with($user->avatar, 'preset:')) {
                Storage::disk('public')->delete($user->avatar);
            }

            $user->avatar = 'preset:' . $request->input('preset_index');
            $user->save();

            return response()->json([
                'message' => 'Avatar updated successfully',
                'avatar_url' => $user->avatar_url
            ]);
        }

        // Handle file upload
        $request->validate([
            'avatar' => 'required|image|mimes:jpeg,png,jpg,gif,webp|max:20480', // Increased to 20MB and added webp
        ]);

        if ($request->hasFile('avatar')) {
            // Delete old avatar if exists
            if ($user->avatar && !str_starts_with($user->avatar, 'preset:')) {
                Storage::disk('public')->delete($user->avatar);
            }

            // Store new avatar
            $path = $request->file('avatar')->store('avatars', 'public');
            $user->avatar = $path;
            $user->save();

            return response()->json([
                'message' => 'Avatar updated successfully',
                'avatar' => $path,
                'avatar_url' => $user->avatar_url // Uses accessor
            ]);
        }

        return response()->json(['message' => 'No file or preset provided'], 400);
    }
}
